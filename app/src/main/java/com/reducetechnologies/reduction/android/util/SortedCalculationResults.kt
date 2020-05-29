package com.reducetechnologies.reduction.android.util

import com.reducetechnologies.calculations_entity.ReducerData

data class SortedCalculationResults(
    val simple: ArrayList<ReducerData>,
    val weight : ArrayList<ReducerData>,
    val volume: ArrayList<ReducerData>,
    val sumAw : ArrayList<ReducerData>,
    val diffSGD: ArrayList<ReducerData>,
    val uDesc : ArrayList<ReducerData>,
    val hrcMin : ArrayList<ReducerData>
)

data class ResultListContainer(val list: ArrayList<ReducerData>)
