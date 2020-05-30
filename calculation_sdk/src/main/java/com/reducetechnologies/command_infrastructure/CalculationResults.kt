package com.reducetechnologies.command_infrastructure

import com.reducetechnologies.calculations_entity.ReducerData

sealed class CalculationResults

data class CalculationResultsContainer(val reducersDataList: ArrayList<ReducerData>) : CalculationResults()

object NoVariants : CalculationResults()

object FinishRequested : CalculationResults()