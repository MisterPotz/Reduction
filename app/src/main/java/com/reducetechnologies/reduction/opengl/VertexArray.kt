package com.reducetechnologies.reduction.opengl

import android.opengl.GLES30
import com.reducetechnologies.reduction.opengl.Constants.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray(vertexData: FloatArray) {
    private val floatBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(vertexData)

    fun setVertexAttribPointer(
        dataOffset: Int,
        attributeLocation: Int,
        componentCount: Int, stride: Int
    ) {
        floatBuffer.position(dataOffset)
        GLES30.glVertexAttribPointer(
            attributeLocation, componentCount, GLES30.GL_FLOAT,
            false, stride, floatBuffer
        )
        GLES30.glEnableVertexAttribArray(attributeLocation)
        floatBuffer.position(0)
    }
}
