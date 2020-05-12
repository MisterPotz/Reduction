package com.reducetechnologies.reduction.geometry

/**
 * Contains coordinate data in [vertices] where
 */
class SparsePointArray(val vertices: FloatArray, val coordinatesPerPoint: Int) {
    private val matrixBuilder = MatrixBuilder()

    fun indexOfPoint(i: Int): Int {
        return vertices.getPointOffset(i, coordinatesPerPoint)
    }

    fun pointsAmount(): Int {
        return vertices.pointsAmount(coordinatesPerPoint)
    }

    operator fun get(i: Int, element: Int = 0): Int {
        if (element >= coordinatesPerPoint) {
            throw IllegalAccessException("element of point cannot be of other point")
        }
        return indexOfPoint(i) + element
    }

    fun scaleAndCommit(x: Float, y: Float, z: Float) {
        val matrix = matrixBuilder.buildMatrix {
            scale(x, y, z)
        }
        commit(matrix)
    }

    fun translateAndCommit(x: Float, y: Float, z: Float) {
        val matrix = matrixBuilder.buildMatrix {
            translate(x, y, z)
        }
        commit(matrix)
    }

    fun rotateAndCommit(degree: Float, x: Float, y: Float, z: Float) {
        val matrix = matrixBuilder.buildMatrix {
            rotate(degree, x, y, z)
        }
        commit(matrix)
    }

    fun commit(matrix: FloatArray) {
        vertices.applyMatrix(matrix, coordinatesPerPoint)
    }
}