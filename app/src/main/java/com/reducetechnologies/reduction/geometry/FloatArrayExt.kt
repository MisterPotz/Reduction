package com.reducetechnologies.reduction.geometry

import android.opengl.Matrix

fun FloatArray.getPointOffset(i: Int, coordinatesPerPoint: Int): Int {
    return if (i > 0) (coordinatesPerPoint) * i else 0
}

fun FloatArray.pointsAmount(coordinatesPerPoint: Int): Int {
    return size / (coordinatesPerPoint)
}

fun FloatArray.applyMatrix(matrix: FloatArray, coordinatesPerPoint: Int) {
    val tempVec = FloatArray(4)
    tempVec[3] = 1f

    for (i in 0 until pointsAmount(coordinatesPerPoint)) {
        val index = getPointOffset(i, coordinatesPerPoint)
        System.arraycopy(this, index, tempVec, 0, coordinatesPerPoint)
        Matrix.multiplyMV(tempVec, 0, matrix, 0, tempVec, 0)
        System.arraycopy(tempVec, 0, this, index, coordinatesPerPoint)
    }
}

fun FloatArray.scale(x:Float, y:Float, z: Float, coordinatesPerPoint: Int) {
    val matrixBuilder = MatrixBuilder()
    val matrix = matrixBuilder.buildMatrix {
        scale(x, y, z)
    }
    applyMatrix(matrix, coordinatesPerPoint)
}

fun FloatArray.translate(x:Float, y:Float, z: Float, coordinatesPerPoint: Int) {
    val matrixBuilder = MatrixBuilder()
    val matrix = matrixBuilder.buildMatrix {
        translate(x, y, z)
    }
    applyMatrix(matrix, coordinatesPerPoint)
}

fun FloatArray.rotate(degrees : Float, x:Float, y:Float, z: Float, coordinatesPerPoint: Int) {
    val matrixBuilder = MatrixBuilder()
    val matrix = matrixBuilder.buildMatrix {
        rotate(degrees, x, y, z)
    }
    applyMatrix(matrix, coordinatesPerPoint)
}

operator fun FloatArray.plus(vertices: FloatArray) : FloatArray {
    val newArr = FloatArray(vertices.size + this.size)
    System.arraycopy(this, 0, newArr, 0, size)
    System.arraycopy(vertices, 0, newArr, size, vertices.size)
    return newArr
}