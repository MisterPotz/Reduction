package com.reducetechnologies.reduction

import androidx.test.platform.app.InstrumentationRegistry

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import androidx.test.ext.junit.runners.AndroidJUnit4
/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.reducetechnologies.reduction", appContext.packageName)
    }

    @Test
    fun getAssetsDirectories() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    }
}
