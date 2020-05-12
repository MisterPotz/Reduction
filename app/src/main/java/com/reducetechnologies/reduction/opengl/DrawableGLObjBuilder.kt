package com.reducetechnologies.reduction.opengl

import android.opengl.GLES30
import com.reducetechnologies.reduction.geometry.Circle
import com.reducetechnologies.reduction.geometry.Point


class DrawableGLObjBuilder {
    var currentFirstIndex = 0
    var floatArray: FloatArray? = null
    var drawPrimitives: MutableList<DrawPrimitive> = mutableListOf()

    /**
     * [relativeForThis] - relative position of connection place to current vertices set
     * [relativeForAppended] - relative position of connection place to the connected vertices set
     */
    fun addPoint(relativeForThis: Point, relativeForAppended: Point, point: Point) {
        val resPoint = point - relativeForAppended + relativeForThis
        floatArray = floatArray!! + resPoint.toVec()
        drawPrimitives.add(DrawPrimitive(currentFirstIndex, floatArray!!.size) {
            GLES30.glDrawArrays(GLES30.GL_POINTS, it.start, it.stopExclusive)
        })
        currentFirstIndex = floatArray!!.size
        val some = IntRange(0, 100)
    }

    fun addCircle(relativeForThis: Point, relativeForAppended: Point, circle: Circle) {

    }
}