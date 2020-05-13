package com.reducetechnologies.reduction.geometry

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.StrictMath.round
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest= Config.NONE)
class SparsePointArrayTest {
    @org.junit.Test
    fun scale() {
        val array = SparsePointArray(floatArrayOf(1f,1f,1f),3)
        array.scaleAndCommit(10f, 10f, 10f)
        Arrays.equals(floatArrayOf(10f,10f,10f),array.vertices ).let {
            assertTrue(it)
        }
    }

    @org.junit.Test
    fun translate() {
        val array = SparsePointArray(floatArrayOf(1f,1f,1f),3)
        array.translateAndCommit(10f, 5f, 1f)
        Arrays.equals(floatArrayOf(11f,6f,2f),array.vertices ).let {
            assertTrue(it)
        }
    }

    @org.junit.Test
    fun scaleAndTranslate() {
        val array = SparsePointArray(floatArrayOf(1f,1f,1f),3)
        array.translateAndCommit(5f,-5f,1f)
        array.scaleAndCommit(10f, 10f, 1f)
        Arrays.equals(floatArrayOf(60f,-40f,2f),array.vertices ).let {
            assertTrue(it)
        }
    }

    @org.junit.Test
    fun rotate() {
        val array = SparsePointArray(floatArrayOf(1f,0f, 0f, 1f, 1f, 0f ,0f, 1f, 0f),3)
        array.rotateAndCommit(90f,1f,0f, 0f)
        array.vertices.roundForTest()
        Arrays.equals(floatArrayOf(1f,0f, 0f
                                ,1f,0f, 1f,
                                0f, 0f, 1f),array.vertices ).let {
            assertTrue(it)
        }
    }

    @org.junit.Test
    fun scaleAndTranslateAndCommit() {
        val array = SparsePointArray(floatArrayOf(1f,1f,1f),3)
        val matrix = MatrixBuilder().buildMatrix {
            translate(10f,10f,10f)
            scale(2f, 2f,1f)
        }
        array.commit(matrix)
        Arrays.equals(floatArrayOf(12f,12f,11f),array.vertices ).let {
            assertTrue(it)
        }
    }

    @org.junit.Test
    fun rotateScaleAndTranslate() {
        val array = SparsePointArray(floatArrayOf(1f,1f,0f, 0f, 0f, 1f),3)
        val matrix = MatrixBuilder().buildMatrix {
            rotate(90f, 1f, 0f, 0f)
            scale(2f, 2f,3f)
            translate(10f,10f,10f)
        }
        array.commit(matrix)
        Arrays.equals(floatArrayOf(12f,10f, 13f, 10f, 8f, 10f), array.vertices ).let {
            assertTrue(it)
        }
    }

    @org.junit.Test
    fun translateRotateAndScale() {
        val array = SparsePointArray(floatArrayOf(1f,0f,0f, 1f, 0f, 1f),3)
        val matrix = MatrixBuilder().buildMatrix {
            translate(10f,0f,0f)
            rotate(90f, 1f, 0f, 0f)
            scale(2f, 2f,3f)
        }
        array.commit(matrix)
        array.vertices.roundForTest()
        Arrays.equals(floatArrayOf(22f,0f, 0f, 22f, -2f, 0f), array.vertices ).let {
            assertTrue(it)
        }
    }

    private fun FloatArray.roundForTest() {
        for (i in this.indices) {
            this[i] = round(this[i] * 10000f) / 10000f
        }
    }
}