package com.reducetechnologies.reduction.geometry

import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

interface ToVertices {
    fun toVertices(): FloatArray
}

data class Point(
    val x: Float,
    val y: Float,
    val z: Float
) : ToVertices {
    val coordinatePerPoint: Int = 3
    operator fun plus(point: Point): Point {
        return Point(point.x + x, point.y + y, point.z + z)
    }

    operator fun minus(point: Point): Point {
        return Point(x - point.x, y - point.y, z - point.z)
    }

    operator fun times(point: Point): Point {
        return Point(x * point.x, y * point.y, z * point.z)
    }

    override fun toVertices(): FloatArray {
        val newVertices = FloatArray(coordinatePerPoint)
        newVertices[0] = x
        newVertices[1] = y
        newVertices[2] = z

        return newVertices
    }
}

class Circle(val center: Point, val radius: Float) : ToVertices {
    var resolution: Int = 200 // amount of points for circle
    val coordinatesPerPoint: Int = 3
    override fun toVertices(): FloatArray {
        val res = resolution
        // one vertex to store the central one, and another one to connect the last one and the first
        val newVertices = FloatArray((res + 1 + 1) * coordinatesPerPoint)
        var angle = 0f
        val firstCirclePoint = 1
        fillPointAt(newVertices, 0, center.x, center.y, center.z)
        Timber.i("Center at $center")
        for (i in firstCirclePoint until  res + firstCirclePoint ) {
            angle = 2 * Math.PI.toFloat() / (res) * (i - firstCirclePoint)
            fillPointAt(
                newVertices,
                i,
                cos(angle) * radius + center.x,
                sin(angle) * radius + center.y,
                center.z
            )
        }
        val firstCirclePointOffset = newVertices.getPointOffset(firstCirclePoint, coordinatesPerPoint)
        fillPointAt(
            newVertices,
            res + firstCirclePoint,
            newVertices[firstCirclePointOffset],
            newVertices[firstCirclePointOffset + 1],
            newVertices[firstCirclePointOffset + 2]
        )
        return newVertices
    }

    /**
     * Moves center to the given point, returns new circle
     */
    fun moveCenterTo(point: Point) : Circle {
        return Circle(point, radius)
    }

    fun fillPointAt(floatArray: FloatArray, i: Int, x: Float, y: Float, z: Float) {
        val index = floatArray.getPointOffset(i, coordinatesPerPoint)
        floatArray[index] = x; floatArray[index + 1] = y; floatArray[index + 2] = z
    }
}

data class Cylinder(val base: Circle, val height: Float)

data class Parallelepiped(val center: Point, val dx: Float, val dy: Float, val dz: Float)

data class Cube(val center: Point, val d: Float)