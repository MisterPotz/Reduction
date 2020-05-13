package com.reducetechnologies.reduction.opengl

import android.opengl.GLES30
import com.reducetechnologies.reduction.geometry.Circle
import com.reducetechnologies.reduction.geometry.Point
import com.reducetechnologies.reduction.geometry.pointsAmount
import com.reducetechnologies.reduction.opengl.PositionableGLObj.Companion.COORDINATES_PER_POINT

class DrawableGLObjBuilder {
    private var currentComponentCount = 0
    private var floatArray: FloatArray? = null
    private var drawPrimitives: MutableList<DrawPrimitive> = mutableListOf()

    /**
     * [relativeForThis] - relative position of connection place to current vertices set
     * [relativeForAppended] - relative position of connection place to the connected vertices set
     */
    fun addPoint(relativeForThis: Point, relativeForAppended: Point, point: Point) {
        val resPoint = point - relativeForAppended + relativeForThis
        floatArray = floatArray!! + resPoint.toVertices()
        drawPrimitives.add(DrawPrimitive(currentComponentCount, currentComponentCount + 1) {
            GLES30.glDrawArrays(GLES30.GL_POINTS, it.offset, it.components)
        })
        currentComponentCount++
    }

    fun addCircle(relativeForThis: Point, relativeForAppended: Point, circle: Circle) {
        val resCenter = circle.center - relativeForAppended + relativeForThis
        val resCircle = circle.moveCenterTo(resCenter)
        val circleVertices = resCircle.toVertices()
        floatArray = floatArray!! + circleVertices
        val circleComponentCount = circleVertices.pointsAmount(COORDINATES_PER_POINT)
        drawPrimitives.add(DrawPrimitive(currentComponentCount, circleComponentCount) {
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, it.offset, it.components)
        })
        currentComponentCount += circleComponentCount
    }

    fun buildPositionableObj(block: DrawableGLObjBuilder.() -> Unit): PositionableGLObj {
        currentComponentCount = 0
        floatArray = FloatArray(0)
        drawPrimitives.clear()
        block()
        return PositionableGLObj(floatArray!!, drawPrimitives)
    }
}