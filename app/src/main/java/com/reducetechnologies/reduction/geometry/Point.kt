package com.reducetechnologies.reduction.geometry

data class Point(
    val x: Float,
    val y: Float,
    val z : Float) {

    operator fun plus(point: Point) : Point {
        return Point(point.x + x, this.y + y, this.z + z)
    }
    operator fun minus(point: Point) : Point {
        return Point(x - point.x, y - point.y, z - point.z)
    }
    operator fun times(point: Point) : Point {
        return Point(x * point.x, y * point.y, z * point.z)
    }
    fun toVec() : FloatArray {
        val newVertices = FloatArray(4) { 1f }
        newVertices[0] = x
        newVertices[1] = y
        newVertices[2] = z
        return newVertices
    }
}

data class Circle(val center: Point, val radius: Float)

data class Cylinder(val base: Circle, val height: Float)

data class Parallelepiped(val center: Point, val dx: Float, val dy: Float, val dz: Float)

data class Cube(val center: Point, val d : Float)