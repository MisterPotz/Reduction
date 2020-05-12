package com.reducetechnologies.reduction.opengl

/**
 * To draw the object, the real drawing functions depends on purposes: texture, simple color, etc.
 * To draw something, you may need to bind the data first (all the vertices, textures) and because
 * these things are too dependent on subtypes of ShaderProgram, such methods are not included in this
 * interface
 */
interface OpenGlObject {
    fun draw()
}