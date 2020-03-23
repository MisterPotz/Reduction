//package com.reducetechnologies.reducerLogic
//
//import com.reducetechnologies.interfacesAndAbstractions.Engine
//import com.reducetechnologies.interfacesAndAbstractions.Shaft
//import com.reducetechnologies.interfacesAndAbstractions.Stage
//import com.reducetechnologies.interfacesAndAbstractions.Wheel
//import com.reducetechnologies.specificationsAndRequests.*
//import java.lang.IllegalArgumentException
//import java.lang.IllegalStateException
//import kotlin.math.max
//import kotlin.math.min
//import kotlin.math.pow
//import kotlin.math.sqrt
//
//class CylindricReducerFabric(reducerRequest: ReducerCreationRequest) :
//    ReducerFabric(reducerRequest) {
//    init {
//        if (reducerRequest.reducerType != Specifications.ReducerType.CYLINDRICAL) {
//            Timber.e("Reducer is of wrong type, cannot be created")
//            throw IllegalArgumentException("Wrong type of reducer")
//
//        }
//    }
//
//    override fun createReducer(): Reducer {
//        return object : Reducer() {
//            override var reducerType: Specifications.ReducerType? = reducerRequest.reducerType
//            override var stagesAmount: Specifications.StagesAmount? = null
//
//            override var u: Float? = null
//            override var ed: Engine? = null
//            override var stages: List<Stage>? = null
//            override var shafts: List<Shaft>? = null
//
//            fun DOPN (LH: Float, u: Float, NRR: Int, ZETR: Float, V: Float = 3f, M: Float = 3f,
//                      stage: Stage
//            ) {
//                //NRR нужно получать в запросе и передавать в параметры редуктора(?)
//                //Логика для получения всех показателей колеса, связанных с материалом (должно
//                //реализовываться отдельной функцией
//                //Я не должен сюда передавать ТУЧУ переменных, я передам сюда просто ступень,
//                //колёса которой уже будут обладать необходимыми характеристиками материала,
//                //которые они получат в другой функции(?)
//                //Логика для определения KHE и KFE по таблицам
//                val KHE: Float = 1f
//                val KFE: Float = 1f
//                //
//                val NF0: Float = 4_000_000f//базовое число циклов
//                val SF: Float = 1.75f//при вероятности неразрушения до 99%, свыше SF>=2 тогда
//                val YR: Float = 1f//для фрезерованых и шлифованых зубьев, для полир - 1.2
//                //подумать над логикой для SF и YR
//                var NS: Float
//                if (stage.wheels[1].N != null && stage.wheels[1].NZAC != null) {
//                    NS = 60*LH*stage.wheels[1].N!!*stage.wheels[1].NZAC!!
//                } else throw IllegalStateException("N or NZAC (or both) in wheel 1 was null")
//                val NHE2: Float = NS*KHE*(NRR + 1)//эквивалентное число циклов при расчёте на вынссл
//                var NHE1: Float
//                if (stage.wheels[0].NZAC != null && stage.wheels[1].NZAC != null) {
//                    NHE1 = NHE2*u*stage.wheels[0].NZAC!!/stage.wheels[1].NZAC!!
//                } else throw IllegalStateException("NZAC in wheel 0 or 1 (or both) was null")
//                //эквивалентное число циклов для шестерни
//                var NHEArr: Array<Float> = arrayOf(NHE1, NHE2)
//                var NFEArr: Array<Float> = Array(2) {0f}
//                //Объявление некоторых переменных, которые не нужны в выводе и требуются только
//                //для некоторых промежуточных расчётов
//                /**
//                 * [SH] - Коэф безопасности при расчёте на контактную прочность
//                 * [SGH0] - длительный предел выносливости при контактных напряжениях
//                 * [SGF0] - длительный предел выносливости при контактных напряжениях
//                 * [POKST], [POKSTF] - показатели степени
//                 */
//                var SH: Float?
//                var SGH0: Float?
//                var SGF0: Float?
//                var POKST: Float?
//                var POKSTF: Float?
//                //Начало основного цикла
//                for(i in 0..1) {
//                    var NH0: Float
//                    if (stage.wheels[i].HRC != null) {
//                        NH0 = 340 * (stage.wheels[i].HRC!!.pow(3.15f)) + 8_000_000f
//                    } else throw IllegalStateException("HRC in wheel $i was null")
//                    //Правильно ли обработал сейчас (?)
//                    if (stage.wheels[i].HRC!! > 35) {
//                        //если больше 50
//                        if (stage.wheels[i].HRC!! >= 50) {
//                            SH = 1.2f
//                            SGH0 = 23*stage.wheels[i].HRC!!
//                            SGF0 = 850f
//                            stage.wheels[i].SGHMD = 40*stage.wheels[i].HRC!!.toInt()
//                            stage.wheels[i].SGFMD = 1450
//                            POKST = 1/6f
//                            POKSTF = 1/9f
//                            if (stage.wheels[i].KFC != 1f)
//                                stage.wheels[i].KFC = 0.90f
//                        } else {
//                            SH = 1.2f
//                            SGH0 = 17*stage.wheels[i].HRC!! + 200
//                            SGF0 = 550f
//                            stage.wheels[i].SGHMD = 40*stage.wheels[i].HRC!!.toInt()
//                            stage.wheels[i].SGFMD = 1430
//                            POKST = 1/6f
//                            POKSTF = 1/6f
//                        }// если между 35 и 50
//                        if (stage.wheels[i].KFC != 1f)
//                            stage.wheels[i].KFC = 0.75f
//                    } else {
//                        SH = 1.1f
//                        SGH0 = 20*stage.wheels[i].HRC!! + 70
//                        SGF0 = 18*stage.wheels[i].HRC!!
//                        if (stage.wheels[i].SGT != null) {
//                            stage.wheels[i].SGHMD = (2.8*stage.wheels[i].SGT!!).toInt()
//                        } else throw IllegalStateException("SGT in wheel $i was null")
//                        stage.wheels[i].SGFMD = (27.4*stage.wheels[i].HRC!!).toInt()
//                        POKST = 1/6f
//                        POKSTF = 1/6f
//                        if (stage.wheels[i].KFC != 1f)
//                            stage.wheels[i].KFC = 0.65f
//                    }
//                    //Начало немного другой логики, уже задались некоторыми параметрами
//                    if (NHEArr[i] > NH0)
//                        NHEArr[i] = NH0
//                    var KHL: Float = (NH0/NHEArr[i]).pow(POKST)
//                    if (KHL >= 2.6f && stage.wheels[i].HRC!! <= 35)
//                        KHL = 2.6f
//                    else if (KHL >= 1.8f && stage.wheels[i].HRC!! > 35)
//                        KHL = 1.8f
//                    var ZETV: Float? //учитывает окружную скорость
//                    if (V > 5 && stage.wheels[i].HRC!! > 35)
//                        ZETV = 0.925f*(V.pow(0.05f))
//                    else if (V > 5)
//                        ZETV = 0.85f*(V.pow(0.1f))
//                    else
//                        ZETV = 1f
//                    //Определение ещё некоторых характеристик колеса
//                    stage.wheels[i].SGHD = ((SGH0/SH)*KHL*ZETR*ZETV).toInt()
//                    if (stage.wheels[i].N != null && stage.wheels[i].NZAC != null) {
//                        NS = 60*LH*stage.wheels[i].N!!*stage.wheels[i].NZAC!!
//                    } else throw IllegalStateException("N or NZAC in wheel $i were null")
//                    if (stage.wheels[i].HRC!! > 35)
//                        NFEArr[i] = NS*KFE*(NRR + 1.2f)
//                    else
//                        NFEArr[i] = NS*KFE*(NRR + 1.1f)
//                    if (NFEArr[i] > NF0)
//                        NFEArr[i] = NF0
//                    var KFL: Float = (NF0/NFEArr[i]).pow(POKSTF)
//                    if (KFL >= 2.08 && stage.wheels[i].HRC!! <= 35)
//                        KFL = 2.08f
//                    else if (KFL > 1.63 && stage.wheels[i].HRC!! > 35)
//                        KFL = 1.63f
//                    var YSG: Float
//                    if (M == 3f)
//                        YSG = 1f
//                    else
//                        YSG = 1.18f - 0.1f* sqrt(M) + 0.006f*M
//                    //Определение ещё некоторых характеристик колеса
//                    if (stage.wheels[i].KFC != null) {
//                        stage.wheels[i].SGFD = ((SGF0/SF)*stage.wheels[i].KFC!!*KFL*YSG*YR).toInt()
//                    } else throw IllegalStateException("KFC in wheel $i was null")
//                }
//                //Вышли из цикла
//                //NP - это индекс типа передачи (см спецификацию)
//                //Следующая строчка - САМЫЙ НЕПОНЯТНЫЙ МОМЕНТ!!! Нужно правильно выбрать SGHMD
//                if (stage.wheels[0].SGHMD != null && stage.wheels[1].SGHMD != null) {
//                    stage.SGHMD = max(stage.wheels[0].SGHMD!!, stage.wheels[1].SGHMD!!)
//                } else throw IllegalStateException("SGHMD in wheel 0 or 1 (or both) was null")
//                if (stage.wheels[0].wheelSubtype.NP < 3) {
//                    if (stage.wheels[0].HRC!! > stage.wheels[1].HRC!! + 7) {
//                        if (stage.wheels[1].HRC!! < 35) {
//                            stage.SGHD =
//                                (0.45 * (stage.wheels[0].HRC!! + stage.wheels[1].HRC!!)).toInt()
//                            when (stage.wheels[0].wheelType) {
//                                Specifications.WheelType.CYLINDRICAL -> if (stage.SGHD!! >
//                                    1.23*stage.wheels[1].HRC!!) {
//                                    stage.SGHD = (1.23*stage.wheels[1].HRC!!).toInt()
//                                    return
//                                }
//                                Specifications.WheelType.CONE -> if (stage.SGHD!! >
//                                    1.15*stage.wheels[1].HRC!!) {
//                                    stage.SGHD = (1.15*stage.wheels[1].HRC!!).toInt()
//                                    return
//                                }
//                            }
//                        }
//                    }
//                }
//                if (stage.wheels[0].SGHD != null && stage.wheels[1].SGHD != null) {
//                    stage.SGHD = min(stage.wheels[0].SGHD!!, stage.wheels[1].SGHD!!)
//                } else throw IllegalStateException("SGHD in wheel 0 or 1 (or both) was null")
//                return
//            }
//            override fun calculate() {
//                // u получать либо из request (если это теоретическое), либо после расчета, если
//                // это получившееся
//                reducerType = reducerRequest.reducerType
//                u = 10f
//                // У нас пока только одна фабрика под энжин, поэтому здесь и использовал ее непосредственно
//                ed = object : EngineFabric(reducerRequest.engine) {}.createEngine()
//                // Фабрик для Stages у нас несколько, поэтому делегировал задачу подбора нужной фабрики
//                // селектору
//                stages = CylindricStagesFabricSelector.getFabric(
//                    reducerRequest.stagesRequest
//                ).let {
//                    stagesAmount = it.stagesRequest.stagesAmount
//                    it.createStages()
//                }
//            }
//        }
//    }
//
//    private object CylindricStagesFabricSelector :
//        StagesFabricSelector {
//        override fun getFabric(stagesRequest: StagesRequest): StagesFabric {
//            return when (stagesRequest.stagesAmount) {
//                Specifications.StagesAmount.SINGLE -> CylindricSingleStagesFabric(
//                    stagesRequest
//                )
//                Specifications.StagesAmount.DOUBLE -> CylindricDoubleStagesFabric(
//                    stagesRequest
//                )
//                else -> Timber.e("Fuck off, nigga! I don't have 3 stages fabric yet").run {
//                    throw IllegalStateException("Fabric for 3 stages is not implemented yet")
//                }
//            }
//        }
//    }
//
//    /**
//     * Субкласс от StagesFabric, который создает одноступенчатые редуктора
//     */
//    class CylindricSingleStagesFabric(stagesRequest: StagesRequest) : StagesFabric(stagesRequest) {
//        /**
//         * Проверка входных данных
//         */
//        init {
//            if (stagesRequest.stagesAmount != Specifications.StagesAmount.SINGLE) {
//                Timber.e("${stagesRequest.stagesAmount} does not equal to SINGLE")
//                throw IllegalArgumentException("can't create CylindricSingleStages fabric")
//            }
//        }
//
//        // Какая-то своя логика для одноступенчатых...
//        override fun createStages(): List<Stage> {
//            return stagesRequest.stageRequests.map {
//                CylindricCommonStageFabric(
//                    it
//                ).createStage()
//            }
//        }
//    }
//
//    /**
//     * Субкласс от StagesFabric, который создает двухступенчатые редуктора
//     */
//    class CylindricDoubleStagesFabric(stagesRequest: StagesRequest) : StagesFabric(stagesRequest) {
//        /**
//         * Проверка входных данных
//         */
//        init {
//            if (stagesRequest.stagesAmount != Specifications.StagesAmount.DOUBLE) {
//                Timber.e("${stagesRequest.stagesAmount} does not equal to DOUBLE")
//                throw IllegalArgumentException("can't create CylindricDoubleStages fabric")
//            }
//        }
//
//        // Какая-то своя логика для двухступенчатых...
//        override fun createStages(): List<Stage> {
//            return stagesRequest.stageRequests.map {
//                CylindricCommonStageFabric(
//                    it
//                ).createStage()
//            }
//        }
//    }
//
//    class CylindricCommonStageFabric(stageRequest: StageRequest) : StageFabric(stageRequest) {
//        // Какая-то своя логика
//        override fun createStage(): Stage {
//            return object :
//                Stage() {
//                override var wheels: List<Wheel> =
//                    (stageRequest.wheelsRequest).map {
//                        CylindricWheelFabric(
//                            it
//                        ).createWheel()
//                    }
//
//                override var stageType: Specifications.StageType = stageRequest.stageType
//            }
//        }
//    }
//
//    class CylindricWheelFabric(wheelRequest: WheelRequest) : WheelFabric(wheelRequest) {
//        override fun createWheel(): Wheel {
//            return object :
//                Wheel() {
//                override var wheelType: Specifications.WheelType =
//                    Specifications.WheelType.CYLINDRICAL
//                override var wheelSubtype: Specifications.WheelSubtype =
//                    wheelRequest.wheelSubtype//можно делать так или для всех видов цилиндрических
//                //колёс создавать свои подфабрики?
//                        override var SGHD: Int? = null
//                override var SGHMD: Int? = null
//                override var SGFD: Int? = null
//                override var SGFMD: Int? = null
//                override var HRC: Float? = null
//                override var N: Float? = null
//                override var SGT: Float? = null
//                override var NZAC: Int? = null
//                override var KFC: Float? = null
//
//                override var geometricOne: Float? = 1.0f // Какая-то первая геометрическая величина
//                override var geometricTwo: Float? = 1.5f // Какая-то вторая геометрическая величина
//            }
//        }
//    }
//}