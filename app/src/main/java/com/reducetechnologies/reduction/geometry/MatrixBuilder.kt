package com.reducetechnologies.reduction.geometry

import android.opengl.Matrix

class MatrixBuilder {
    private val identityMatrix: FloatArray = FloatArray(16)
    private val tempMatrix = MathUtil.createIdentityMatrix()

    fun buildMatrix(block: MatrixBuilder.() -> Unit) : FloatArray {
        Matrix.setIdentityM(identityMatrix, 0)
        block()
        val newArr = FloatArray(16)
        System.arraycopy(identityMatrix, 0, newArr, 0, 16)
        Matrix.setIdentityM(identityMatrix, 0)
        return newArr
    }

    fun scale(x: Float, y: Float, z:Float) {
        Matrix.setIdentityM(tempMatrix, 0)
        Matrix.scaleM(tempMatrix, 0, x, y, z)
        Matrix.multiplyMM(identityMatrix, 0, tempMatrix, 0, identityMatrix,0)
    }

    fun translate(x: Float, y : Float, z : Float) {
        Matrix.setIdentityM(tempMatrix, 0)
        Matrix.translateM(tempMatrix, 0, x, y, z)
        Matrix.multiplyMM(identityMatrix, 0, tempMatrix, 0, identityMatrix,0)
    }

    fun rotate(degree: Float, x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(tempMatrix, 0)
        Matrix.rotateM(tempMatrix,0,  degree, x, y, z)
        Matrix.multiplyMM(identityMatrix, 0, tempMatrix, 0, identityMatrix,0)
    }
}