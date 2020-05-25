package com.reducetechnologies.command_infrastructure.p_screens

import com.reducetechnologies.calculations_entity.ReducerData
import com.reducetechnologies.command_infrastructure.*

internal class OutputPScreen {
    private val pScreen: PScreen
    private val reducerData: ArrayList<ReducerData>

    constructor(reducerData: ArrayList<ReducerData>) {
        this.reducerData = reducerData
        pScreen = PScreen(
            title = "OutputData",
            fields = getFields()
        )
    }

    fun getPScreen() : PScreen {
        return pScreen
    }

    companion object {
        protected fun getFields() : List<PField> {
            return listOf(
                PField(
                    pFieldType = PFieldType.TEXT,
                    typeSpecificData = TextSpec(
                        text = "Расчитанные варианты редукторов",
                        additional = AdditionalText(TextType.HEADLINE)
                    ),
                    fieldId = 0
                ),
                PField(
                    pFieldType = PFieldType.LINK,
                    typeSpecificData = LinkSpec(text = "Лучший результат",where = DestinationResult),
                    fieldId = 1
                ),
                PField(
                    pFieldType = PFieldType.LINK,
                    typeSpecificData = LinkSpec(text = "Сортированные по весу",where = DestinationSortedResultList(Sorted.WEIGHT)),
                    fieldId = 2
                ),
                PField(
                    pFieldType = PFieldType.LINK,
                    typeSpecificData = LinkSpec(text = "Сортмрованные по объему",where = DestinationSortedResultList(Sorted.VOLUME)),
                    fieldId = 3
                ),
                PField(
                    pFieldType = PFieldType.LINK,
                    typeSpecificData = LinkSpec(text = "Сортированные по убыванию точности передаточного отношения", where = DestinationSortedResultList(Sorted.U_DESC)),
                    fieldId = 4
                ),
                PField(
                    pFieldType = PFieldType.LINK,
                    typeSpecificData = LinkSpec(text = "Сортированные по межосевому расстоянию",where =  DestinationSortedResultList(Sorted.SUM_AW)),
                    fieldId = 5
                ),
                PField(
                    pFieldType = PFieldType.LINK,
                    typeSpecificData = LinkSpec(text = "Сортированные по твердости",where =  DestinationSortedResultList(Sorted.SUM_HRC)),
                    fieldId = 6
                ),
                PField(
                    pFieldType = PFieldType.LINK,
                    typeSpecificData = LinkSpec(text = "Сортированные по разнице SGD-SG",where =  DestinationSortedResultList(Sorted.DIFF_SGD_SG)),
                    fieldId = 7
                )
            )
        }

    }
}