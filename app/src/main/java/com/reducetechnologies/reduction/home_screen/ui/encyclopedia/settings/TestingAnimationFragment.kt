package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.settings

import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.GLES32
import android.opengl.GLUtils
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.reducetechnologies.reduction.R
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.opengles.GL11

class TestingAnimationFragment : Fragment() {

    private lateinit var mTextureView: TextureView
    private lateinit var mVertices: FloatBuffer
    private val mVerticesData = floatArrayOf(
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        0.0f, 1.0f, 0.0f
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_testing_animation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mVertices = ByteBuffer.allocateDirect(mVerticesData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertices.put(mVerticesData).position(0)

        mTextureView = view.findViewById(R.id.textureView)
        mTextureView.surfaceTextureListener = GLSurfaceTextureListener()
    }

    private inner class RenderThread(private val mSurface: SurfaceTexture) : Thread() {
        private var mEglDisplay: EGLDisplay? = null
        private var mEglSurface: EGLSurface? = null
        private var mEglContext: EGLContext? = null
        private var mProgram = 0
        private var mEgl: EGL10? = null
        private var mGl: GL11? = null
        override fun run() {
            initGL()
            val attribPosition = GLES30.glGetAttribLocation(
                mProgram,
                "position"
            )
            checkGlError()
            GLES30.glEnableVertexAttribArray(attribPosition)
            checkGlError()
            GLES30.glUseProgram(mProgram)
            checkGlError()
            while (true) {
                checkCurrent()
                mVertices.position(0)
                GLES30.glVertexAttribPointer(
                    attribPosition, 3,
                    GLES30.GL_FLOAT, false, 12, mVertices
                )
                checkGlError()
                GLES30.glClearColor(0.0f, 0.0f, 0f, 1f)
                checkGlError()

                // Draw background color
                GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

                checkGlError()
                GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
                Log.d(TAG, "!!")
                checkGlError()
                if (!mEgl!!.eglSwapBuffers(mEglDisplay, mEglSurface)) {
                    Log.e(TAG, "cannot swap buffers!")
                }
                checkEglError()
                try {
                    sleep(3000)
                } catch (e: InterruptedException) {
                    // Ignore
                }
            }
        }

        private fun checkCurrent() {
            if (!mEglContext!!.equals(mEgl!!.eglGetCurrentContext())
                || !mEglSurface!!.equals(
                    mEgl!!
                        .eglGetCurrentSurface(EGL10.EGL_DRAW)
                )
            ) {
                checkEglError()
                if (!mEgl!!.eglMakeCurrent(
                        mEglDisplay, mEglSurface,
                        mEglSurface, mEglContext
                    )
                ) {
                    throw RuntimeException(
                        "eglMakeCurrent failed "
                                + GLUtils.getEGLErrorString(
                            mEgl!!
                                .eglGetError()
                        )
                    )
                }
                checkEglError()
            }
        }

        private fun checkEglError() {
            val error = mEgl!!.eglGetError()
            if (error != EGL10.EGL_SUCCESS) {
                Log.e(
                    TAG,
                    "EGL error = 0x" + Integer.toHexString(error)
                )
            }
        }

        private fun checkGlError() {
            val error = GLES30.glGetError()
            if (error != GLES30.GL_NO_ERROR) {
                Log.e(
                    TAG,
                    "GL error = 0x" + Integer.toHexString(error)
                )
            }
        }

        private fun buildProgram(vertexSource: String, fragmentSource: String): Int {
            val vertexShader = buildShader(
                GLES30.GL_VERTEX_SHADER,
                vertexSource
            )
            if (vertexShader == 0) {
                return 0
            }
            val fragmentShader = buildShader(
                GLES30.GL_FRAGMENT_SHADER, fragmentSource
            )
            if (fragmentShader == 0) {
                return 0
            }
            val program = GLES30.glCreateProgram()
            if (program == 0) {
                return 0
            }
            GLES30.glAttachShader(program, vertexShader)
            checkGlError()
            GLES30.glAttachShader(program, fragmentShader)
            checkGlError()
            GLES30.glLinkProgram(program)
            checkGlError()
            val status = IntArray(1)
            GLES30.glGetProgramiv(
                program, GLES30.GL_LINK_STATUS, status,
                0
            )
            checkGlError()
            if (status[0] == 0) {
                Log.e(TAG, GLES30.glGetProgramInfoLog(program))
                GLES30.glDeleteProgram(program)
                checkGlError()
            }
            return program
        }

        private fun buildShader(type: Int, shaderSource: String): Int {
            val shader = GLES30.glCreateShader(type)
            if (shader == 0) {
                return 0
            }
            GLES30.glShaderSource(shader, shaderSource)
            checkGlError()
            GLES30.glCompileShader(shader)
            checkGlError()
            val status = IntArray(1)
            GLES30.glGetShaderiv(
                shader, GLES30.GL_COMPILE_STATUS, status,
                0
            )
            if (status[0] == 0) {
                Log.e(TAG, GLES30.glGetShaderInfoLog(shader))
                GLES30.glDeleteShader(shader)
                return 0
            }
            return shader
        }

        private fun initGL() {
            val vertexShaderSource = """attribute vec4 position;
void main () {
   gl_Position = position;
   gl_PointSize = 40.0;
}"""
            val fragmentShaderSource = """precision mediump float;
void main () {
   gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}"""
            mEgl = EGLContext.getEGL() as EGL10

            mEglDisplay = mEgl!!.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
            if (mEglDisplay === EGL10.EGL_NO_DISPLAY) {
                throw RuntimeException(
                    "eglGetDisplay failed "
                            + GLUtils.getEGLErrorString(mEgl!!.eglGetError())
                )
            }
            val version = IntArray(2)
            if (!mEgl!!.eglInitialize(mEglDisplay, version)) {
                throw RuntimeException(
                    "eglInitialize failed "
                            + GLUtils.getEGLErrorString(mEgl!!.eglGetError())
                )
            }
            Timber.i("Versions: ${version[0]}  -- ${version[1]}")
            val configsCount = IntArray(1)
            val configs: Array<EGLConfig?> = arrayOfNulls<EGLConfig>(1)
            val configSpec = intArrayOf(
                EGL10.EGL_RENDERABLE_TYPE,
                EGL_OPENGL_ES2_BIT ,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE
            )
            var eglConfig: EGLConfig? = null
            require(
                mEgl!!.eglChooseConfig(
                    mEglDisplay, configSpec, configs, 1,
                    configsCount
                )
            ) {
                ("eglChooseConfig failed "
                        + GLUtils.getEGLErrorString(
                    mEgl!!
                        .eglGetError()
                ))
            }
            if (configsCount[0] > 0) {
                eglConfig = configs[0]
            }
            if (eglConfig == null) {
                throw RuntimeException("eglConfig not initialized")
            }
            val attrib_list = intArrayOf(
                EGL_CONTEXT_CLIENT_VERSION, 3, EGL10.EGL_NONE
            )
            mEglContext = mEgl!!.eglCreateContext(
                mEglDisplay,
                eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list
            )
            checkEglError()
            mEglSurface = mEgl!!.eglCreateWindowSurface(
                mEglDisplay, eglConfig, mSurface, null
            )
            checkEglError()
            if (mEglSurface == null || mEglSurface === EGL10.EGL_NO_SURFACE) {
                val error = mEgl!!.eglGetError()
                if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                    Log.e(
                        TAG,
                        "eglCreateWindowSurface returned EGL10.EGL_BAD_NATIVE_WINDOW"
                    )
                    return
                }
                throw RuntimeException(
                    "eglCreateWindowSurface failed "
                            + GLUtils.getEGLErrorString(error)
                )
            }
            if (!mEgl!!.eglMakeCurrent(
                    mEglDisplay, mEglSurface,
                    mEglSurface, mEglContext
                )
            ) {
                throw RuntimeException(
                    "eglMakeCurrent failed "
                            + GLUtils.getEGLErrorString(mEgl!!.eglGetError())
                )
            }
            checkEglError()
            mGl = mEglContext!!.getGL() as GL11
            checkEglError()
            mProgram = buildProgram(
                vertexShaderSource,
                fragmentShaderSource
            )
        }
        private  val EGL_OPENGL_ES2_BIT = 4
        private  val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        private  val TAG = "RenderThread"
    }

    private inner class GLSurfaceTextureListener : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(
            surface: SurfaceTexture,
            width: Int, height: Int
        ) {
            Timber.i("SurfaceTexture available")
            RenderThread(surface).start()
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int, height: Int
        ) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }
}
