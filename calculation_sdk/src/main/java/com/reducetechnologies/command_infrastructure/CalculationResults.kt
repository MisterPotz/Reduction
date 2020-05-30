package com.reducetechnologies.command_infrastructure

import com.reducetechnologies.calculations_entity.ReducerData

sealed class CalculationResults

data class CalculationResultsContainer(val reducersDataList: ArrayList<ReducerData>) : CalculationResults()

sealed class Error : CalculationResults()

object FinishedEarly: Error()

object CalculationNotPossible : Error()