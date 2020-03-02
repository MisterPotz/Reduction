package com.reducetechnologies.calculation_util

import mu.KotlinLogging
import java.lang.StringBuilder


private val logger = KotlinLogging.logger { }

/**
 * Shouldn't be constructed directly outside of this class
 * TODO("Добавить красивый стектрейс в случае ошибки на каком-либо селекторе")
 */
class SelectionTree private constructor(
    private val string: String = "root",
    // должен знать о дереве, в котором состоит
    private val higherSelectionTree: SelectionTree? = null,

    // у дерева есть кондишены, которые определяют поток действий и разветвления
    private val conditions: List<() -> Boolean> = listOf(),

    // У дерева есть действия, выбираемые по кондишенам. Размер обоих массивов должен быть одинаковый
    private val actions: List<SelectionTree.() -> Unit> = listOf()
) {
    // у дерева могут быть внутренние деревья
    private val innerTrees: MutableList<SelectionTree> = mutableListOf<SelectionTree>()

    // Используется для записи вызовов селекторов на этом уровне иерархии
    private val stringBuilder by lazy { StringBuilder() }

    // есть освобождающий путь развития
    // есть пути, зависящие от кондишенов
    // аксиома - каждый кондишен уникальный, ни один из кондишенов не может повторяться в рамках одного ветвления

    /**
     * Constructs new SelectionTree and then performs selection
     */
    fun select(
        selectionName: String,
        conditions: List<() -> Boolean>,
        actions: List<SelectionTree.() -> Unit>
    ) {
        if (conditions.size != actions.size && conditions.size + 1 != actions.size)
            throw Exception("wrong sizes")
        // Настраиваем связи между селекшн-деревьями
        val newSelectionTree = SelectionTree(selectionName, this, conditions, actions)
        // Логгируем создание матчера
        newSelectionTree.logMatcher(newSelectionTree.string)
        innerTrees.add(newSelectionTree)
        newSelectionTree.matchAndExecute()
    }

    fun select(
        selectionName: String,
        builder: TreeBuilder
    ) {
        select(selectionName, builder.conditions, builder.actions)
    }

    /**
     * Вызывает кондишены и делает проверки на баги (например, несколько кондишенов выполнились)
     */
    private fun matchAndExecute() {
        var oneIsTrue = false
        var index = 0
        for (condition in conditions.withIndex()) {
            if (condition.value.invoke()) {
                if (oneIsTrue) {
                    throw Exception("More than one is true")
                }
                oneIsTrue = true
                index = condition.index
            }
        }
        // Если ни одно из условий не выполнилось - просто возвратиться
        if (!oneIsTrue){
            return
        }
        // Логика для случая когда ни одно из условий не выполнилось и количество условий на единицу
        // иеньше количества экшнов. Если количество экшнов и условий не равно по-другому, это ошибка
        if (conditions.size + 1 == actions.size && !oneIsTrue) {
            index = actions.size - 1
        }
        // Логируем выполнение метода
        logMethod("method: ${index} invoked")
        actions[index].invoke(this)
    }

    /**
     * Чуть ниже прописаны методы для логирования. Логирование подразделяется на два типа:
     * 1) То, которое в рантайме - то есть как событие произошло - логируется сразу
     * 2) То, которое записывает логи, и потом сразу логирует все события
     * Преимущество первого - в том что можно видеть логи из экшонов, сопоставленные по времени
     * с логами матчеров. Это может понадобится, поэтому такой функционал есть.
     * Преимущество второго - в репрезентабельности порядка вызовов матчеров. Можно увидеть дерево
     * решений, которое в итоге выросло
     */
    private fun logMatcher(string: String) =
        logMatcherHistory(string).run { logMatcherRealTime(string) }

    private fun logMethod(string: String) =
        logMethodHistory(string).run { logMethodRealTime(string) }

    private fun logMatcherHistory(string: String) = logHistory(string, offset = 0)

    private fun logMethodHistory(string: String) = logHistory(string, offset = 1)

    private fun logMatcherRealTime(string: String) = logRealTime(string, offset = 0)

    private fun logMethodRealTime(string: String) = logRealTime(string, offset = 1)
    // Сохранить в буфер для последующего лога второго типа
    private inline fun saveToBuffer(string: () -> String) = stringBuilder.append(string())

    // Сделать сообщение о событии с учетом отступа и уровня иерархии
    private inline fun buildMsg(offset: Int = 1, string: () -> String): String {
        val stringBuilder = StringBuilder()
        getHierarchyLevel().let {
            (0 until it).forEach {
                stringBuilder.append(divider)
            }
            (0 until offset).forEach {
                stringBuilder.append(smallDivider)
            }
            stringBuilder.append(string())
        }
        return stringBuilder.toString()
    }

    // Метод для логирования второго типа в историю
    private fun logHistory(string: String, toRoot: Boolean = false, offset: Int = 0) {
        if (!logHierarchyAfterwards) return
        if (higherSelectionTree == null) {
            saveToBuffer { string + "\n" }
        } else if (toRoot) {
            higherSelectionTree.logHistory(string)
        } else {
            higherSelectionTree.logHistory(
                string = buildMsg(offset = offset) { string },
                toRoot = true
            )
        }
    }

    // Для лога реального времени
    private fun logRealTime(string: String, offset: Int) {
        if (logHierarchyInTime) {
            logger.info { buildMsg(offset = offset) { string } }
        }
    }

    /**
     * Позволяет получить уровень иерархии для этого дерева
     */
    private fun getHierarchyLevel(): Int {
        return if (higherSelectionTree == null) {
            0
        } else {
            higherSelectionTree.getHierarchyLevel() + 1
        }
    }

    // Отобразить записанный исторический лог
    private fun executeLog() {
        if (logHierarchyAfterwards) {
            logger.info { stringBuilder.toString() }
        }
        stringBuilder.clear()
    }

    companion object {
        // Символ для мини отступов (для логов методов)
        private val smallDivider = "."

        // Для уровня иерархии матчеров
        private val divider = "..."
        /**
         * Флаг, чтобы после выполнения всей цепочки отобразить иерархию соверешенных вызовов
         */
        private val logHierarchyAfterwards = true

        private val logHierarchyInTime = true

        // создает корневой селекшн, передает в созданную область родительский элемент
        fun rootSelection(selectionBuilder: SelectionTree.() -> Unit) {
            // корневой объект селекшн рут
            val root = SelectionTree()

            root.logMatcher(root.string)

            //вызывает на корневой элемент область селекшн билдера
            root.selectionBuilder()
            root.executeLog()
        }
    }
}

class TreeBuilder {
    val conditions: MutableList<() -> Boolean> = mutableListOf()
    val actions: MutableList<SelectionTree.() -> Unit> = mutableListOf()

    /**
     * Condition
     */
    fun c(cond: () -> Boolean): TreeBuilder {
        conditions.add(cond)
        return this
    }

    /**
     * Action
     */
    fun a(action: SelectionTree.() -> Unit): TreeBuilder {
        actions.add(action)
        return this
    }

    companion object {
        val build: TreeBuilder
            get() = TreeBuilder()
    }
}

fun `как делается по тупому`() {
    var a: Int = 5
    var b: Int = 8
    SelectionTree.rootSelection {
        select(
            "1st matcher",
            listOf(
                { a <= 5 && b > 7 },
                { a <= 5 && b == 7 }),
            listOf<SelectionTree.() -> Unit>(
                {
                    println("a is $a <= 5 && b is $b > 7")
                }, {
                    println("a is $a <= 5 && b is $b >= 7")
                    select(
                        "2nd matcher",
                        listOf({ a == 4 }, { a == 2 }, { b == 6 }),
                        listOf<SelectionTree.() -> Unit>(
                            { println("first condition") },
                            { println("second condition") },
                            { println("three ") })
                    )
                    println("in 2nd body")
                })
        )
        println("in 1 st body")
    }
}

fun `как делается по-пацански в натуре йоу`() {
    var `твердость колеса`: Int = 350
    var `твердость шестерни`: Int = 360
    //далее следует какая-то тупая, абсолютно ебейшая логика со своими селекторами
    // пусть оно будет медленнее, но зато логи кошерные
    SelectionTree.rootSelection {
        select("Сравнение тупого говна", TreeBuilder.build
            .c { `твердость колеса` <= 350 && `твердость шестерни` <= 350 }
            .c { `твердость колеса` <= 350 && `твердость шестерни` >= 350 }
            .c { `твердость колеса` > 350 && `твердость шестерни` >= 350 }
            .a { logger.info { "Победил Леликов" } }
            .a {
                logger.info { "Победил Дунаев" }
                // Еще тупая логика, а дерево решений-то все растет, мать его
                select("Следующий этап сравнений говен", TreeBuilder.build
                    .c { 2 + 1 == 4 }
                    .c { 0 + 0 == 1 }
                    .a {
                        println("Победила математика")
                        // ПОжалуй еще посравниваем, это так весело!
                        select("Просто фан", TreeBuilder.build
                            .c { false }
                            .c { true }
                            .a { logger.info { false } }
                            .a { logger.info { true } })
                    }
                    .a { println("Победил Шильников") }
                    .a { println("Победила Вселенная (или  просто else - ведь всего знать нельзя!") })
                logger.info { "Еще какая-то логика, хз зачем" }
                select("Проверочный селектор", TreeBuilder.build
                    .c { 13 >= 4 }
                    .c { 2 > 3 }
                    .a { }
                    .a { }
                    .a { println("Здесь нет ошибки -  выполнится в случае else") }
                    /*.a { println("А вот здесь уже будет") }*/)
            }
            .a { logger.info { "some other logics" } })
        println("in root body")
    }
}

fun main(args: Array<String>) {
    `как делается по-пацански в натуре йоу`()
}