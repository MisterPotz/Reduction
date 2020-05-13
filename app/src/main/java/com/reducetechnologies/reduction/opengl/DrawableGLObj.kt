package com.reducetechnologies.reduction.opengl

import com.reducetechnologies.reduction.opengl.Constants.BYTES_PER_FLOAT

/**
 * Contains vertices and contains draw commands for opengl
 */
abstract class  DrawableGLObj<T : ShadingProgramAtributes>(val vertices: FloatArray, val drawPrimitives : List<DrawPrimitive>){
        protected val vertexArray = VertexArray(vertices)
    fun bindAndDraw(programToBind : HasAttributeVertexPosition) {
        bind(programToBind)
        drawAll()
    }
    protected abstract fun bind(programToBind: HasAttributeVertexPosition)
    protected fun drawAll() {
        for (i in drawPrimitives) {
            i.draw()
        }
    }
}

class PositionableGLObj(vertices: FloatArray,
                        drawPrimitives: List<DrawPrimitive>,
                        val coordinatesPerPoint : Int = COORDINATES_PER_POINT) : DrawableGLObj<HasAttributeVertexPosition>(vertices, drawPrimitives) {
    override fun bind(programToBind: HasAttributeVertexPosition) {
        vertexArray.setVertexAttribPointer(
            0,
            programToBind.getAVertexPosition(),
            COORDINATES_PER_POINT,
            STRIDE
        )
    }

    companion object {
        const val COORDINATES_PER_POINT = 3
        const val STRIDE = COORDINATES_PER_POINT * BYTES_PER_FLOAT
    }
}

