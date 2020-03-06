package com.reducetechnologies.calculation_util

import com.reducetechnologies.calculation_util.SelectionTreeTest.Scope.action1
import com.reducetechnologies.calculation_util.SelectionTreeTest.Scope.action2
import mu.KotlinLogging
import org.junit.jupiter.api.Test

private val logger = KotlinLogging.logger { }

internal class SelectionTreeTest {

    object Scope {
        var var1: Int = 3
        var var2: Int = 5

        val condition1 = { var1 <= 2 && var2 <= 5 }
        val condition2 = { var1 > 2 && var2 <= 5 }
        val action1: SelectionTree.() -> Unit = {
            logger.info("var1 : ${Scope.var1}")
        }
        val action2: SelectionTree.() -> Unit = {
            logger.info("var2 : ${Scope.var2}")
            var1 = 1
        }
    }

    @Test
    fun selectorCreationTest() {
        SelectionTree.rootSelection {
            for (i in 0..1) {
                select("testing selector", TreeBuilder.build
                    .c(Scope.condition1)
                    .c(Scope.condition2)
                    .a(action1)
                    .a(action2)
                    .a {}
                )
                Scope.var1 = 1
            }
        }
    }

    private fun `complex case`() {
        var var1: Int = 3
        var var2: Int = 5
        //далее следует какая-то тупая, абсолютно ебейшая логика со своими селекторами
        // пусть оно будет медленнее, но зато логи кошерные
        SelectionTree.rootSelection {
            select("Selector match first", TreeBuilder.build
                .c { var1 <= 2 && var2 <= 5 }
                .c { var1 > 2 && var2 <= 5 }
                .c { var1 >= 5 && var2 <= 10 }
                .a { logger.info { "Победил Леликов" } }
                .a {
                    logger.info { "Победил Дунаев" }
                    // Еще тупая логика, а дерево решений-то все растет, мать его
                    select("Selector first - first", TreeBuilder.build
                        .c { 2 + 1 == 4 }
                        .c { 0 + 0 == 1 }
                        .a {
                            println("Победила математика")
                            // ПОжалуй еще посравниваем, это так весело!
                            select("Selector first - first - first", TreeBuilder.build
                                .c { false }
                                .c { true }
                                .a { logger.info { false } }
                                .a { logger.info { true } })
                        }
                        .a { logger.info("Победил Шильников") }
                        .a { logger.info("Победила Вселенная (или  просто else - ведь всего знать нельзя!") })
                    logger.info { "Еще какая-то логика, хз зачем" }
                    select("Selector first - second", TreeBuilder.build
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

    @Test
    fun complexCase() {
        `complex case`()
    }
}