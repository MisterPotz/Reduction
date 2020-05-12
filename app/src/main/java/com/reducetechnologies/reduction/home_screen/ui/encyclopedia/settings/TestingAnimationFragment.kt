package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.settings

import android.graphics.SurfaceTexture
import android.opengl.GLES20.*
import android.opengl.GLES30
import android.opengl.GLUtils
import android.opengl.Matrix.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.opengl.ColorShaderProgram
import com.reducetechnologies.reduction.opengl.Mallet
import com.reducetechnologies.reduction.opengl.MatrixHelper
import com.reducetechnologies.reduction.opengl.ShaderHelper.checkCurrent
import com.reducetechnologies.reduction.opengl.ShaderHelper.checkEglError
import timber.log.Timber
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.opengles.GL11

class TestingAnimationFragment : Fragment() {

    private lateinit var mTextureView: TextureView
    private val projectionMatrix = FloatArray(16)

    private val modelMatrix = FloatArray(16)
    private var colorProgram: ColorShaderProgram? = null

    private val testingMallet = Mallet()

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
        mTextureView = view.findViewById(R.id.textureView)
        mTextureView.surfaceTextureListener = GLSurfaceTextureListener()
    }

    private inner class RenderThread(
        private val mSurface: SurfaceTexture,
        val width: Int,
        val height: Int
    ) : Thread() {
        private var mEglDisplay: EGLDisplay? = null
        private var mEglSurface: EGLSurface? = null
        private var mEglContext: EGLContext? = null
        private var mEgl: EGL10? = null
        private var mGl: GL11? = null

        override fun run() {
            initGL()
            reinitModel(width, height)
            colorProgram = ColorShaderProgram(
                context!!,
                R.raw.simple_vertex_shader,
                R.raw.simple_fragment_shader
            )
            checkCurrent(
                egl = mEgl,
                eglContext = mEglContext,
                eglDisplay = mEglDisplay,
                eglSurface = mEglSurface
            )

            colorProgram!!.initProgram()

            checkEglError(mEgl!!)
            while (true) {
                checkCurrent(
                    egl = mEgl,
                    eglContext = mEglContext,
                    eglDisplay = mEglDisplay,
                    eglSurface = mEglSurface
                )

//                glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
                // Draw background color
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

                colorProgram!!.useProgram()
                colorProgram!!.setUniforms(projectionMatrix)
                testingMallet.bindData(colorProgram!!)
                testingMallet.draw()
                checkEglError(mEgl!!)
                if (!mEgl!!.eglSwapBuffers(mEglDisplay, mEglSurface)) {
                    Log.e(TAG, "cannot swap buffers!")
                }
                checkEglError(mEgl!!)
                try {
                    sleep(3000)
                } catch (e: InterruptedException) {
                    // Ignore
                }
            }
        }

        private fun initGL() {
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
                EGL_OPENGL_ES2_BIT,
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
            checkEglError(mEgl!!)
            mEglSurface = mEgl!!.eglCreateWindowSurface(
                mEglDisplay, eglConfig, mSurface, null
            )
            checkEglError(mEgl!!)
            if (mEglSurface == null || mEglSurface === EGL10.EGL_NO_SURFACE) {
                val error = mEgl!!.eglGetError()
                if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                    Timber.w(
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
            checkEglError(mEgl!!)
            mGl = mEglContext!!.getGL() as GL11
            checkEglError(mEgl!!)
        }

        private val EGL_OPENGL_ES2_BIT = 4
        private val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        private val TAG = "RenderThread"
    }

    fun reinitModel(width: Int, height: Int) {
        glClearColor(0.1f, 0.1f, 0.1f, 1f);
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(
            projectionMatrix, 45.0f, width.toFloat()
                    / height.toFloat(), 1f, 10f
        )
        setIdentityM(modelMatrix, 0)

        translateM(modelMatrix, 0, 0f, 0f, -2.5f)
        //rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
    }

    private inner class GLSurfaceTextureListener : SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(
            surface: SurfaceTexture,
            width: Int, height: Int
        ) {
            Timber.i("SurfaceTexture available")
            RenderThread(surface, width, height).start()
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int, height: Int
        ) {
            Timber.i("In onSurfaceTextureSizeChanged")
            reinitModel(width, height)
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }
}
