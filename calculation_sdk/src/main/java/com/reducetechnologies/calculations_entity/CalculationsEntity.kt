package com.reducetechnologies.calculations_entity

import com.reducetechnologies.calculations.InputData
import com.reducetechnologies.calculations.ZCREDMethodsClass
import javax.inject.Inject

class CalculationsEntity constructor(private val inputData: InputData,
                                     @Inject private val zcredMethod: ZCREDMethodsClass) {

}