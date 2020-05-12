package com.reducetechnologies.reduction.geometry

import android.opengl.Matrix

object MathUtil {
    fun createIdentityMatrix(): FloatArray {
        return FloatArray(16).let {
            Matrix.setIdentityM(it, 0)
            it
        }
    }
}