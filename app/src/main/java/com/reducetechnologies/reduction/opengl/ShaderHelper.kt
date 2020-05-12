package com.reducetechnologies.reduction.opengl

import android.opengl.GLES30
import android.opengl.GLES30.*
import timber.log.Timber
import android.opengl.GLES30.glCompileShader
import android.opengl.GLES30.GL_COMPILE_STATUS
import android.opengl.GLES30.GL_LINK_STATUS
import android.opengl.GLES30.glGetProgramInfoLog
import android.opengl.GLES30.GL_VALIDATE_STATUS
import android.opengl.GLES30.glValidateProgram
import android.opengl.GLUtils
import java.lang.IllegalStateException
import javax.microedition.khronos.egl.*

object ShaderHelper {
    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GL_VERTEX_SHADER, shaderCode)
    }

    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode)
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderObjectId: Int = glCreateShader(type)
        Timber.v("Source code: $shaderCode")
        if (shaderObjectId == 0) {
            Timber.w("Could not create shader")
            return 0
        }
        glShaderSource(shaderObjectId, shaderCode)
        glCompileShader(shaderObjectId)
        val compileStatus = IntArray(1)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)
        // If it failed, delete the shader object.
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId)
            Timber.w("Compiling of shader failed")
            return 0
        }
        Timber.v("Results of compiling source:\n ${glGetShaderInfoLog(shaderObjectId)}")
        return shaderObjectId
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId: Int = glCreateProgram()
        if (programObjectId == 0) {
            Timber.w("Can't create OpenGL program")
            return 0;
        }
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);
        glLinkProgram(programObjectId)

        val linkStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)
        Timber.v("Results of linking program:\n ${glGetProgramInfoLog(programObjectId)}")

        if (linkStatus[0] == 0) { // If it failed, delete the program object.
            glDeleteProgram(programObjectId);
            Timber.w("Can't link program")
            return 0;
        }
        return programObjectId;
    }

    fun validateProgram(programObjectId: Int): Boolean {
        glValidateProgram(programObjectId)
        val validateStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
        Timber.v(
            "Results of validating program: ${validateStatus[0]}\nLog: ${glGetProgramInfoLog(
                programObjectId
            )}"
        )
        return validateStatus[0] != 0
    }

    fun buildProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)
        return linkProgram(
            vertexShader,
            fragmentShader
        ).let { if (validateProgram(it)) it else throw IllegalStateException("Validation failed") }
    }

    fun checkEglError(egl: EGL10) {
        val error = egl.eglGetError()
        if (error != EGL10.EGL_SUCCESS) {
            Timber.w("EGL error = 0x%s", Integer.toHexString(error))
        }
    }

    fun checkGlError() {
        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            Timber.w("GL error = 0x%s", Integer.toHexString(error))
        }
    }

     fun checkCurrent(eglContext: EGLContext?, eglSurface: EGLSurface?, eglDisplay: EGLDisplay?, egl: EGL10?) {
        if (!eglContext!!.equals(egl!!.eglGetCurrentContext())
            || !eglSurface!!.equals(
                egl
                    .eglGetCurrentSurface(EGL10.EGL_DRAW)
            )
        ) {
            checkEglError(egl)
            if (!egl.eglMakeCurrent(
                    eglDisplay, eglSurface,
                    eglSurface, eglContext
                )
            ) {
                throw RuntimeException(
                    "eglMakeCurrent failed "
                            + GLUtils.getEGLErrorString(
                        egl
                            .eglGetError()
                    )
                )
            }
            checkEglError(egl)
        }
    }
}