package com.reducetechnologies.command_infrastructure.p_screens

import com.reducetechnologies.calculations_entity.ReducerData
import com.reducetechnologies.command_infrastructure.*

internal class OutputPScreen {
    private val pScreen: PScreen
    private val outputPScreenFields: OutputPScreenFields
    private val reducerData: ArrayList<ReducerData>

    constructor(reducerData: ArrayList<ReducerData>) {
        this.reducerData = reducerData
        outputPScreenFields = OutputPScreenFields( this.reducerData.mapIndexed { index, it ->
           it.toString()
        })
        pScreen = PScreen(
            title = "OutputData",
            fields = outputPScreenFields.getFields()
        )
    }

    fun getPScreen() : PScreen {
        return pScreen
    }
}

internal class OutputPScreenFields {
    private val fields: MutableList<PField>
    private val variants: PField

    constructor(variants: List<String>) {
        this.variants = setVariants(variants)
        fields = mutableListOf(this.variants)
    }
    //Пока только единственное поле
    private fun setVariants(variants: List<String>): PField {
        return PField(
            pFieldType = PFieldType.TEXT,
            typeSpecificData = TextListSpec(
                title = "Расчитанные варианты редукторов: ",
                additionalTextList = AdditionalTextList(
                    options = variants
                )
                //Потом заполнить encyclopediaID
            ),
            fieldId = 1
        )
    }

    fun getFields(): List<PField> {
        return fields
    }
}