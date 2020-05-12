package com.reducetechnologies.reduction.opengl

/**
 * Contains vertices and contains draw commands for opengl
 */
abstract class DrawableGLObj<T : ShaderProgram>(val vertices: FloatArray, val drawPrimitives : List<DrawPrimitive>){
    abstract fun bindAndDraw(programToBind : T)
    protected abstract fun bind(programToBind: T)
    protected fun drawAll() {
        for (i in drawPrimitives) {
            i.draw()
        }
    }
}


