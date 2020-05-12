package com.reducetechnologies.reduction.opengl

/**
 * Accepts [DrawPrimitive] in order to obtain its start and stop positions for drawing
 */
typealias DrawFromTo = (DrawPrimitive) -> Unit

data class DrawPrimitive(val start : Int, val stopExclusive: Int, private val command: DrawFromTo) {
    /**
     * Internally passes [this] to command.
     */
    fun draw() {
        command(this)
    }
}