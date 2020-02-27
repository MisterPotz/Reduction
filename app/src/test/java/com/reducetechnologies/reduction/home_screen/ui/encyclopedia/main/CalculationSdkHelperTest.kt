//package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main
//
//import androidx.arch.core.executor.ArchTaskExecutor
//import androidx.arch.core.executor.TaskExecutor
//import com.reducetechnologies.command_infrastructure.*
//import com.reducetechnologies.reduction.di.CalculationSdkComponent
//import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationSdkHelper
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.slot
//import org.junit.jupiter.api.Test
//
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.extension.AfterEachCallback
//import org.junit.jupiter.api.extension.BeforeEachCallback
//import org.junit.jupiter.api.extension.ExtendWith
//import org.junit.jupiter.api.extension.ExtensionContext
//import org.junit.jupiter.api.fail
//import java.lang.IllegalStateException
//import javax.inject.Provider
//
//class InstantExecutorExtension : BeforeEachCallback, AfterEachCallback {
//    override fun beforeEach(context: ExtensionContext?) {
//        ArchTaskExecutor.getInstance()
//            .setDelegate(object : TaskExecutor() {
//                override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
//
//                override fun postToMainThread(runnable: Runnable) = runnable.run()
//
//                override fun isMainThread(): Boolean = true
//            })
//    }
//
//    override fun afterEach(context: ExtensionContext?) {
//        ArchTaskExecutor.getInstance().setDelegate(null)
//    }
//
//}
//// extension for livedata
//@ExtendWith(InstantExecutorExtension::class)
//internal class CalculationSdkHelperTest {
//    val wPscreen = WrappedPScreen(
//        PScreen("test", listOf(PField(PFieldType.TEXT, TextSpec("text"), 0))),
//        true
//    )
//    val calculationSdk = mockk<CalculationSdk>() {
//        every { init() } answers {
//            wPscreen
//        }
//        var init = slot<PScreen>()
//        every { validateCurrent(capture(init)) } answers {
//            // resetting slot
//            init = slot()
//            null
//        }
//
//        every { hasNextPScreen() } returns false
//    }
//
//    val calculationSdkHelper =
//        CalculationSdkHelper(
//            Provider {
//                mockk<CalculationSdkComponent.Factory> {
//                    every { build() } answers {
//                        mockk<CalculationSdkComponent> {
//                            every { getBuilder() } answers {
//                                mockk<CalculationSdkBuilder> {
//                                    every { buildSdk(any()) } answers {
//                                        calculationSdk
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            })
//
//    @Test
//    fun isActive() {
////        val listOptions = mutableListOf<CalculationSdkBuilder.Options>()
//        val helper = calculationSdkHelper
//
//        assertTrue(!helper.isActive)
//
//        val outLiveData = helper.startCalculation()
//        assertTrue(helper.isActive)
//        var called = false
//        outLiveData.observeForever {
//            called = true
//            assertEquals(wPscreen, it)
//        }
//
//        if (!called) fail("Observer was not called")
//    }
//
//
//
//    @Test
//    fun fullScenario() {
////        val listOptions = mutableListOf<CalculationSdkBuilder.Options>()
//        val helper = calculationSdkHelper
//
//        assertTrue(!helper.isActive)
//
//        val outLiveData = helper.startCalculation()
//        val inData = helper.getDataForIn()
//        assertTrue(helper.isActive)
//        var called = false
//        // getting new screens
//        outLiveData.observeForever {
//            // setting returned screen
//            inData.value = it.pScreen
//        }
//        // expecting helper to be finished
//        assertTrue(!helper.isActive)
//
//    }
//
//    @Test
//    fun setOnSessionStoppedCallback() {
//        val helper = calculationSdkHelper
//        assertTrue(!helper.isActive)
//        var callbackWorked = false
//        helper.onSessionStopped = {
//            callbackWorked = true
//        }
//
//        val outLiveData = helper.startCalculation()
//        val inData = helper.getDataForIn()
//        assertTrue(helper.isActive)
//        var called = false
//        // getting new screens
//        outLiveData.observeForever {
//            // setting returned screen
//            inData.value = it.pScreen
//        }
//        assertTrue(callbackWorked)
//    }
//
//    @Test
//    fun startAgain() {
//        val helper = calculationSdkHelper
//        assertTrue(!helper.isActive)
//
//        helper.startCalculation()
//
//        var caught = false
//        try {
//            helper.startCalculation()
//        } catch (e : IllegalStateException) {
//            caught = true
//        }
//        assertTrue(!caught)
//    }
//}