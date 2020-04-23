package com.reducetechnologies.calculations

import com.reducetechnologies.calculation_util.SelectionTree
import com.reducetechnologies.calculation_util.TreeBuilder
import com.reducetechnologies.specificationsAndRequests.Specifications
import com.reducetechnologies.tables_utils.table_contracts.SGTTTable
import com.reducetechnologies.tables_utils.table_contracts.FatigueTable
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class DOPN_MethodsClass(val tableSGTT: SGTTTable, val tablekHeFe: FatigueTable) {
    data class Arguments(
        var N2: Float,//так, в книжке написано, что сюда нужно передавать частоту колеса(?)
        var u: Float,//здесь могут быть uT, uB

        var ZETR: Float = 0.95f,
        val KFC: Array<Float> = arrayOf(1f, 1f),//чтобы условие на KFC == 1 не срабатывало
        val inputData: InputData,
        val option: ReducerOptionTemplate
    )

    fun dopn(args: Arguments, dopnScope: DOPNScope) {
        dopnScope.apply {
            //Логика для определения KHE и KFE по таблицам
            val KHE: Float = tablekHeFe.rows[args.inputData.NRR].kHE
            val KFE: Float
            if (args.option.HRC[0] <= 35f){
                KFE = tablekHeFe.rows[args.inputData.NRR].kFE.improv
            } else {
                KFE = tablekHeFe.rows[args.inputData.NRR].kFE.hard
            }
            //
            val NF0: Float = 4_000_000f//базовое число циклов
            val SF: Float = 2f//при вероятности неразрушения до 99%, свыше SF>=2 тогда
            val YR: Float = 1f//для фрезерованых и шлифованых зубьев, для полир - 1.2
            //подумать над логикой для SF и YR
            //Подумать, как правильно получать N1 and N2
            var NS: Float = 60 * args.inputData.LH * args.N2 * args.inputData.NZAC[1]
            val NHE2: Float =
                NS * KHE * (args.inputData.NRR + 1)//эквивалентное число циклов при расчёте на вынссл
            var NHE1: Float = NHE2 * args.u * args.inputData.NZAC[0] / args.inputData.NZAC[1]
            //эквивалентное число циклов для шестерни
            var NHEArr: Array<Float> = arrayOf(NHE1, NHE2)
            var NFEArr: Array<Float> = Array(2) { 0f }
            var NArr: Array<Float> = arrayOf(args.N2*args.u, args.N2)
            //Объявление некоторых переменных, которые не нужны в выводе и требуются только
            //для некоторых промежуточных расчётов
            /**
             * [SH] - Коэф безопасности при расчёте на контактную прочность
             * [SGH0] - длительный предел выносливости при контактных напряжениях
             * [SGF0] - длительный предел выносливости при контактных напряжениях
             * [POKST], [POKSTF] - показатели степени
             */
            var SH: Float?
            var SGH0: Float?
            var SGF0: Float?
            var POKST: Float
            var POKSTF: Float?
            //Начало основного цикла
            for (i in 0..1){
                var NH0: Float = 340 * (args.option.HRC[i].pow(3.15f)) + 8_000_000f
                if (args.option.HRC[i] > 35){
                    if (args.option.HRC[i] >= 50){
                        SH = 1.2f
                        SGH0 = 23 * args.option.HRC[i]
                        SGF0 = 850f
                        wheelsSGHMD[i] = 40 * args.option.HRC[i].toInt()
                        wheelsSGFMD[i] = 1450
                        POKST = 1 / 6f
                        POKSTF = 1 / 9f
                        if (args.KFC[i] != 1f){
                            wheelsKFC[i] = 0.90f
                        }
                    }
                    else {
                        SH = 1.2f
                        SGH0 = 17 * args.option.HRC[i] + 200
                        SGF0 = 550f
                        wheelsSGHMD[i] = 40 * args.option.HRC[i].toInt()
                        wheelsSGFMD[i] = 1430
                        POKST = 1 / 6f
                        POKSTF = 1 / 6f
                        if (args.KFC[i] != 1f){
                            wheelsKFC[i] = 0.75f
                        }
                    }
                }
                else {
                    SH = 1.1f
                    SGH0 = 20 * args.option.HRC[i] + 70
                    SGF0 = 18 * args.option.HRC[i]
                    //Останется время, переделай таблицу sgtt для оптимизации расчёта
                    //wheelsSGHMD
                    wheelsSGHMD[i] =
                        when (args.option.HRC[i]){
                            24.8f -> (2.8*tableSGTT.SGTT[0][i]).toInt()
                            28.5f -> (2.8*tableSGTT.SGTT[0][0]).toInt()
                            49.0f -> (2.8*tableSGTT.SGTT[1][0]).toInt()
                            59.0f -> (2.8*tableSGTT.SGTT[2][0]).toInt()
                            else -> -1//показатель ошибки, такого быть не должно
                        }
                    if ((args.option.HRC[i] == 59.0f)&&(i == 1)){
                        wheelsSGHMD[i] =
                            (2.8*tableSGTT.SGTT[2][1]).toInt()
                    }
                    wheelsSGFMD[i] = (27.4 * args.option.HRC[i]).toInt()
                    POKST = 1 / 6f
                    POKSTF = 1 / 6f
                }
                if (NHEArr[i] > NH0){
                    NHEArr[i] = NH0
                }
                var KHL: Float = (NH0 / NHEArr[i]).pow(POKST)
                if (KHL >= 2.6f && args.option.HRC[i] <= 35){
                    KHL = 2.6f
                }
                else if (KHL >= 1.8f && args.option.HRC[i] > 35){
                    KHL = 1.8f
                }
                val ZETV: Float = if (V > 5 && args.option.HRC[i] > 35){
                    0.925f * (V.pow(0.05f))
                } else if (V > 5){
                    0.85f * (V.pow(0.1f))
                } else {
                    1f
                }
                //Определение ещё некоторых характеристик колеса
                wheelsSGHD[i] = ((SGH0 / SH) * KHL * args.ZETR * ZETV).toInt()
                NS = 60 * args.inputData.LH * NArr[i] * args.inputData.NZAC[i]
                if (args.option.HRC[i] > 35){
                    NFEArr[i] = NS * KFE * (args.inputData.NRR + 1.2f)
                }
                else {
                    NFEArr[i] = NS * KFE * (args.inputData.NRR + 1.1f)
                }
                if (NFEArr[i] > NF0){
                    NFEArr[i] = NF0
                }
                var KFL: Float = (NF0 / NFEArr[i]).pow(POKSTF)
                if (KFL >= 2.08 && args.option.HRC[i] <= 35){
                    KFL = 2.08f
                }
                else if (KFL > 1.63 && args.option.HRC[i] > 35){
                    KFL = 1.63f
                }
                val YSG: Float = if (M == 3f){
                    1f
                } else {
                    1.18f - 0.1f * sqrt(M) + 0.006f * M
                }
                wheelsSGFD[i] = ((SGF0 / SF) * wheelsKFC[i] * KFL * YSG * YR).toInt()
            }
            //Вышли из цикла
            //NP - это индекс типа передачи (см спецификацию)
            //Следующая строчка - САМЫЙ НЕПОНЯТНЫЙ МОМЕНТ!!! Нужно правильно выбрать SGHMD - уже решил
            SGHMD = min(wheelsSGHMD[0], wheelsSGHMD[1])//Всё верно, здесь именно min
            if (args.inputData.NP < 3){
                if (args.option.HRC[0] > args.option.HRC[1] + 7){
                    if (args.option.HRC[1] < 35){
                        SGHD =
                            (0.45 * (args.option.HRC[0] + args.option.HRC[1])).toInt()
                        when (args.inputData.wheelType) {
                            Specifications.WheelType.CYLINDRICAL -> if (SGHD!! >
                                1.23 * args.option.HRC[1]
                            ) {
                                SGHD =
                                    (1.23 * args.option.HRC[1]).toInt()
                            }
                            Specifications.WheelType.CONE -> if (SGHD!! >
                                1.15 * args.option.HRC[1]
                            ) {
                                SGHD =
                                    (1.15 * args.option.HRC[1]).toInt()
                            }
                        }
                    }
                }
            }
            SGHD = min(wheelsSGHD[0], wheelsSGHD[1])//Всё верно, здесь именно min
            return
            /*SelectionTree.rootSelection {
                for (i in 0..1) {
                    var NH0: Float = 340 * (args.option.HRC[i].pow(3.15f)) + 8_000_000f
                    select("Свойства материала",
                        TreeBuilder.build
                            //если больше 50
                            .c { args.option.HRC[i] > 35 }
                            .a {
                                select("HRC >= 50",
                                    TreeBuilder.build
                                        .c { args.option.HRC[i] >= 50 }
                                        .a {
                                            SH = 1.2f
                                            SGH0 = 23 * args.option.HRC[i]
                                            SGF0 = 850f
                                            wheelsSGHMD[i] = 40 * args.option.HRC[i].toInt()
                                            wheelsSGFMD[i] = 1450
                                            POKST = 1 / 6f
                                            POKSTF = 1 / 9f
                                            select("KFC != 1",
                                                TreeBuilder.build
                                                    .c { args.KFC[i] != 1f }
                                                    .a { wheelsKFC[i] = 0.90f }
                                            )
                                        }
                                        .a {
                                            SH = 1.2f
                                            SGH0 = 17 * args.option.HRC[i] + 200
                                            SGF0 = 550f
                                            wheelsSGHMD[i] = 40 * args.option.HRC[i].toInt()
                                            wheelsSGFMD[i] = 1430
                                            POKST = 1 / 6f
                                            POKSTF = 1 / 6f
                                            select("KFC != 1 при HRC <= 35",
                                                TreeBuilder.build
                                                    .c { args.KFC[i] != 1f }
                                                    .a { wheelsKFC[i] = 0.75f }
                                            )
                                        }
                                )
                            }
                            // если между 35 и 50
                            .a {
                                SH = 1.1f
                                SGH0 = 20 * args.option.HRC[i] + 70
                                SGF0 = 18 * args.option.HRC[i]
                                //Останется время, переделай таблицу sgtt для оптимизации расчёта
                                //wheelsSGHMD
                                wheelsSGHMD[i] =
                                    when (args.option.HRC[i]){
                                        24.8f -> (2.8*tableSGTT.SGTT[0][i]).toInt()
                                        28.5f -> (2.8*tableSGTT.SGTT[0][0]).toInt()
                                        49.0f -> (2.8*tableSGTT.SGTT[1][0]).toInt()
                                        59.0f -> (2.8*tableSGTT.SGTT[2][0]).toInt()
                                        else -> -1//показатель ошибки, такого быть не должно
                                    }
                                if ((args.option.HRC[i] == 59.0f)&&(i == 1)){
                                    wheelsSGHMD[i] =
                                        (2.8*tableSGTT.SGTT[2][1]).toInt()
                                }
                                wheelsSGFMD[i] = (27.4 * args.option.HRC[i]).toInt()
                                POKST = 1 / 6f
                                POKSTF = 1 / 6f
                                select("KFC != 1 при 35 < HRC < 50",
                                    TreeBuilder.build
                                        .c { args.KFC[i] != 1f }
                                        .a { wheelsKFC[i] = 0.65f }
                                )
                            }
                    )
                    select("Уже задались некоторыми параметрами",
                        TreeBuilder.build
                            .c { NHEArr[i] > NH0 }
                            .a { NHEArr[i] = NH0 }
                    )
                    var KHL: Float = (NH0 / NHEArr[i]).pow(POKST!!)
                    select("Задание KHL",
                        TreeBuilder.build
                            .c { KHL >= 2.6f && args.option.HRC[i] <= 35 }
                            .c { KHL >= 1.8f && args.option.HRC[i] > 35 }
                            .a { KHL = 2.6f }
                            .a { KHL = 1.8f }
                    )
                    //учитывает окружную скорость
                    var ZETV: Float? = null
                    select("Задание ZETV",
                        TreeBuilder.build
                            .c { V > 5 && args.option.HRC[i] > 35 }
                            .c { V > 5 }
                            .a { ZETV = 0.925f * (V.pow(0.05f)) }
                            .a { ZETV = 0.85f * (V.pow(0.1f)) }
                            .a { ZETV = 1f }
                    )
                    //Определение ещё некоторых характеристик колеса
                    wheelsSGHD[i] = ((SGH0!! / SH!!) * KHL * args.ZETR * ZETV!!).toInt()
                    NS = 60 * args.inputData.LH * NArr[i] * args.inputData.NZAC[i]
                    select("Определение NFE",
                        TreeBuilder.build
                            .c { args.option.HRC[i] > 35 }
                            .a { NFEArr[i] = NS * KFE * (args.inputData.NRR + 1.2f) }
                            .a { NFEArr[i] = NS * KFE * (args.inputData.NRR + 1.1f) }
                    )
                    select("NFE при невыполнении 1ого условия",
                        TreeBuilder.build
                            .c { NFEArr[i] > NF0 }
                            .a { NFEArr[i] = NF0 }
                    )
                    var KFL: Float = (NF0 / NFEArr[i]).pow(POKSTF!!)
                    select("Задание KFL",
                        TreeBuilder.build
                            .c { KFL >= 2.08 && args.option.HRC[i] <= 35 }
                            .c { KFL > 1.63 && args.option.HRC[i] > 35 }
                            .a { KFL = 2.08f }
                            .a { KFL = 1.63f }
                    )
                    var YSG: Float? = null
                    select("Задание YSG",
                        TreeBuilder.build
                            .c { M == 3f }
                            .a { YSG = 1f }
                            .a { YSG = 1.18f - 0.1f * sqrt(M) + 0.006f * M }
                    )
                    wheelsSGFD[i] = ((SGF0!! / SF) * wheelsKFC[i] * KFL * YSG!! * YR).toInt()
                }
                //Вышли из цикла
                //NP - это индекс типа передачи (см спецификацию)
                //Следующая строчка - САМЫЙ НЕПОНЯТНЫЙ МОМЕНТ!!! Нужно правильно выбрать SGHMD
                SGHMD = min(wheelsSGHMD[0], wheelsSGHMD[1])//Здесь max или min? Пусть пока будет min, так надёжнее
                select("Логика в зависимости от NP",
                    TreeBuilder.build
                        .c { args.inputData.NP < 3 }
                        .a {
                            select("Сравнение HRC",
                                TreeBuilder.build
                                    .c { args.option.HRC[0] > args.option.HRC[1] + 7 }
                                    .a {
                                        select("Задача SGHD в зав от HRC",
                                            TreeBuilder.build
                                                .c { args.option.HRC[1] < 35 }
                                                .a {
                                                    SGHD =
                                                        (0.45 * (args.option.HRC[0] + args.option.HRC[1])).toInt()
                                                    when (args.inputData.wheelType) {
                                                        Specifications.WheelType.CYLINDRICAL -> if (SGHD!! >
                                                            1.23 * args.option.HRC[1]
                                                        ) {
                                                            SGHD =
                                                                (1.23 * args.option.HRC[1]).toInt()
                                                        }
                                                        Specifications.WheelType.CONE -> if (SGHD!! >
                                                            1.15 * args.option.HRC[1]
                                                        ) {
                                                            SGHD =
                                                                (1.15 * args.option.HRC[1]).toInt()
                                                        }
                                                    }
                                                }
                                        )
                                    }
                            )
                        }
                )
                SGHD = min(wheelsSGHD[0], wheelsSGHD[1])//Здесь max или min? Скорее всего min
            }*/
        }
    }
}

data class DOPNScope(
    var V: Float = 3f,
    var M: Float = 3f,
    var SGHD: Int? = null,
    var SGHMD: Int? = null,
    var wheelsSGHD: Array<Int> = Array(2){-1},
    var wheelsSGHMD: Array<Int> = Array(2){-1},
    var wheelsSGFD: Array<Int> = Array(2){-1},
    var wheelsSGFMD: Array<Int> = Array(2){-1},
    var wheelsKFC: Array<Float> = Array(2){1f}//Посмотри ещё и реализуй логику их выбора из учебника, нужно ещё их в таблицу забить
)
