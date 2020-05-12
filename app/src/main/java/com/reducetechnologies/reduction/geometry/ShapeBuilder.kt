package com.reducetechnologies.reduction.geometry

class ShapeBuilder(val coordinatesPerVertex: Int) {
    var floatArray: FloatArray? = null

    fun add(point: Point, addAt: Point, offset: Point) {
        val newVertices = FloatArray(4) { 0f }
        newVertices[3] = 1f
        newVertices[0] = point.x
        newVertices[1] = point.y
        newVertices[2] = point.z
        floatArray!!.appendVertices(addAt, offset, newVertices)
        val some = IntRange(0, 100)
    }

    fun addCircle(circle: Circle, addAt: Point, offset: Point, resolution: Int = 500) {
        // plus one, because
        val newVertices = FloatArray(500 * coordinatesPerVertex + 1)
        for (i in 0 until newVertices.pointsAmount(coordinatesPerVertex) - 1) {

        }
    }
}