package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import com.reducetechnologies.command_infrastructure.WrappedPScreen
import com.reducetechnologies.command_infrastructure.needsInput
import com.reducetechnologies.reduction.home_screen.ui.calculation.CalculationSdkHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.IllegalStateException

/**
 * Хранит в себе все врапы, которые были, включая новые пришедшие
 */
class PScreenSwitcher(
    val helper: CalculationSdkHelper) {
    // fetching all already made wrapped screens
    private var status = helper.getCurrentStatus()

    // index of current - as it is bigger than any index of previous, equals to size
    private var current = status.previous.size
    var currentWasValidatedSuccessfully = false
        private set

    init {
        updateCurrentWasSet()
    }

    private fun get(index: Int): WrappedPScreen {
        if (index >= status.previous.size && status.currentPending == null) {
            throw IllegalStateException("No current pending, previous stack size: ${status.previous.size}, given index: $index")
        }
        return if (index == status.previous.size) {
            status.currentPending!!
        } else {
            status.previous[index]
        }
    }

    private fun isInPrevious(): Boolean = current + 1 < status.previous.size
    private fun hasPending(): Boolean = (status.currentPending != null)
    private fun isBeforePending(): Boolean = current + 1 == status.previous.size
    private fun helperHasNext(): Boolean = helper.hasNext()

    private fun haveNextBackend(): Boolean {
        return isInPrevious() || (hasPending() && isBeforePending()) || helperHasNext()
    }

    /**
     * Нужно для правильного экранного отображения
     */
    fun haveNext(): Boolean {
        return haveNextBackend() && currentWasValidatedSuccessfully
    }

    fun current(): WrappedPScreen {
        return get(current)
    }

    fun havePrevious(): Boolean {
        return (current >= 1)
    }

    fun needsInput(): Boolean {
        return current().pScreen.needsInput()
    }

    private fun fetchUpdates() {
        status = helper.getCurrentStatus()
    }

    suspend fun next(): WrappedPScreen {
        if (!(haveNext())) {
            throw IllegalStateException("Cant return next as it is not yet available")
        }
        if (!(isInPrevious() || (hasPending() && isBeforePending()))) {
            // каждый пскрин должен быть провалидирован, даже если не было ввода
            // поскольку для скринов, где ввод не требуется, кнопка ввода заблокирована, валидация проходит при next
            if (!needsInput()) {
                withContext(Dispatchers.Default) {
                    helper.validate(current().pScreen)
                }
            }
            Timber.i("getting next from helper")
            helper.next()
            fetchUpdates()
        }
        current += 1
        updateCurrentWasSet()
        return current()
    }

    private fun updateCurrentWasSet() {
        // ставить false только в случае если текущий current - самый последний
        if (current == status.previous.size) {
            currentWasValidatedSuccessfully = !needsInput()
        } else {
            currentWasValidatedSuccessfully = true
        }
    }

    /**
     * true if enter successful
     */
    suspend fun enter(): Boolean {
        withContext(Dispatchers.Default) {
            helper.validate(current().pScreen)
        }
        fetchUpdates()
        currentWasValidatedSuccessfully = status.currentPending == null
        return currentWasValidatedSuccessfully
    }

    fun prev(): WrappedPScreen {
        if (!havePrevious()) {
            throw IllegalStateException("Can't set array index to be < 0, current previous list size: ${status.previous.size}")
        }
        current -= 1
        updateCurrentWasSet()
        return current()
    }
}