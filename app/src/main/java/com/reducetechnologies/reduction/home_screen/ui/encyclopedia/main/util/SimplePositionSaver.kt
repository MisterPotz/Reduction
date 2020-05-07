package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.util

import android.os.Parcelable
import android.util.SparseArray

data class SimplePositionSaver(val outer : Parcelable?, val inner : PositionsSaver)

data class PositionsSaver(val saved : SparseArray<Parcelable?>)