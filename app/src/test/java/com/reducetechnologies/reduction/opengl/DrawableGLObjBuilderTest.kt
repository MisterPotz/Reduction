package com.reducetechnologies.reduction.opengl

import com.reducetechnologies.reduction.geometry.Circle
import com.reducetechnologies.reduction.geometry.Point
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest= Config.NONE)
class DrawableGLObjBuilderTest {
    @org.junit.Test
    fun buildSimpleShape() {
        val drawableGLObj = DrawableGLObjBuilder().buildPositionableObj {
            addCircle(Point(0f,0f,0f),
                Point(0f,0f,0f), Circle(Point(0f,0f,0f), 0.5f)
            )
        }
        assertTrue(drawableGLObj != null)
    }

    @org.junit.Test
    fun buildWithRelatives() {
        val drawableGLObj = DrawableGLObjBuilder().buildPositionableObj {
            addPoint(Point(1.0f, 1.0f, 1.0f), Point(-1f, -1f, -1f), Point(0f,0f,0f))
            addCircle(Point(0f,0f,0f),
                Point(0f,0f,0f), Circle(Point(0f,0f,0f), 0.5f)
            )
        }
        assertTrue(drawableGLObj != null)
    }
}