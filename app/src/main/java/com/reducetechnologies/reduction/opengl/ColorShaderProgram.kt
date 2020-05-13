package com.reducetechnologies.reduction.opengl

import android.content.Context
import android.opengl.GLES20.glUniformMatrix4fv
import android.opengl.GLES30
import androidx.annotation.RawRes
import timber.log.Timber

class ColorShaderProgram(context: Context, @RawRes vertexShader: Int,
@RawRes fragmentShader: Int):
    ShaderProgram(context, vertexShader, fragmentShader), HasAttributeVertexPosition {
    private var positionHandle : Int = 0
    private var colorHandle : Int = 0
    private var matrixHandle : Int = 0

    override fun initProgram() {
        super.initProgram()
        positionHandle = GLES30.glGetAttribLocation(program, A_POSITION)
        colorHandle = GLES30.glGetAttribLocation(program, A_COLOR)
        matrixHandle = GLES30.glGetUniformLocation(program, U_MATRIX)
        Timber.i("Handles: $positionHandle, $colorHandle, $matrixHandle")
    }

    fun setUniforms(matrix: FloatArray) {
        Timber.i("Matrix is ${matrix.toList()}")
        glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);
    }

    fun getPositionHandle() : Int {
        return positionHandle
    }
    fun getColorHandle() = colorHandle

    fun getMatrixHandle() = matrixHandle

    override fun getAVertexPosition(): Int {
        return getPositionHandle()
    }
}