package com.reducetechnologies.reduction.opengl
import android.opengl.GLES20
import android.opengl.GLES30
import com.reducetechnologies.reduction.opengl.Constants.BYTES_PER_FLOAT
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

class Mallet : OpenGlObject{
    private val vertexArray: VertexArray = VertexArray(circleData)
    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.getPositionHandle(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            colorProgram.getColorHandle(),
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
    }

    override fun draw() {
        GLES20.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 1001)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val COLOR_COMPONENT_COUNT = 3
        private val STRIDE: Int =
            ((POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
                    * BYTES_PER_FLOAT)
        private val VERTEX_DATA =
            floatArrayOf( // Order of coordinates: X, Y, R, G, B
                0f, -0.4f, 1f, 1f, 1f,
                0f, 0.4f, 1f, 0f, 0f
            )

        private val TRIANGLE_DATA = floatArrayOf(
            -0.5f, -0.5f, 0f, 1f, 0f, 0f,
            0.5f, -0.5f, 0f, 1f, 0f, 0f,
            0.0f, 0.5f, 0f, 1f, 0f, 0f
        )

        private val circleData = kotlin.run {
            val size = 6006
            val vertexSize = size / 6
            val array = FloatArray(size)
            var counter = 0;
            var vertexCounter = 0
            var current = 0f
            array[counter++] = 0f; array[counter++] = 0f; array[counter++] = 0f; array[counter++] = 1f;array[counter++] = 0f;array[counter++] = 0f;
            vertexCounter++
            while (vertexCounter < vertexSize - 1 && counter < size ) {
                current =  2 * Math.PI.toFloat() / (vertexSize) * (vertexCounter - 1)

                array[counter++] =  cos(current) * 0.5f
                array[counter++] = sin(current) * 0.5f
                array[counter++] = 0f
                array[counter++] = 1f
                array[counter++] = 0f
                array[counter++] = 0f;
                vertexCounter++
            }
            array[counter++] = array[6]; array[counter++] = array[7]; array[counter++] = 0f; array[counter++] = 1f; array[counter++] = 0f; array[counter++] = 0f;
            Timber.i("vertexCounter $vertexCounter, counter: $counter")

            Timber.i("Vertices: ${array.toList()}")
            array
        }
    }
}
