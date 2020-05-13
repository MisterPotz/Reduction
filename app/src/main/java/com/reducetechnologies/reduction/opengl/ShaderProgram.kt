package com.reducetechnologies.reduction.opengl

import android.content.Context
import android.opengl.GLES30
import androidx.annotation.RawRes
import com.reducetechnologies.reduction.opengl.TextResourceReader.readTextFileFromResource

interface ShadingProgramAtributes

interface HasUniformMatrixPosition : ShadingProgramAtributes {
    fun getUMatrixpositionhandle() : Int
}

interface HasAttributeVertexPosition : ShadingProgramAtributes {
    fun getAVertexPosition() : Int
}

abstract class ShaderProgram protected constructor(
    private val context: Context?,
    @RawRes
    val vertexShaderResourceId: Int,
    @RawRes
    val fragmentShaderResourceId: Int
) {
    // Shader program
    var program: Int = 0
        private set

    open fun initProgram() {
        program = ShaderHelper.buildProgram(
            readTextFileFromResource(
                context!!, vertexShaderResourceId
            ),
            readTextFileFromResource(
                context, fragmentShaderResourceId
            )
        )
    }

    fun useProgram() {
        // Set the current OpenGL shader program to this program.
        GLES30.glUseProgram(program)
    }

    companion object {
        // Uniform constants
        const val U_MATRIX = "u_Matrix"
//        protected const val U_TEXTURE_UNIT = "u_TextureUnit"

        // Attribute constants
        const val A_POSITION = "a_Position"
        const val A_COLOR = "a_Color"
//        protected const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    }
}
