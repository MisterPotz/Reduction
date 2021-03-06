package com.reducetechnologies.command_infrastructure

import java.lang.IllegalStateException

/**
 * Обеспечивает, что протоскрин должен сначала быть показан - только потом зовет следующий. В конструктор должен передаваться
 * чел, который связан с расчетами, и у которого есть реальные скрины.
 * Т. е., этот класс сдужит гарантом, что скрин не отматается за секунду, а будет показан, потом провалидирован,
 * и тоьлко потом будет запрос к внутреннему челу.
 * Кроме того, хранит бывшие протоскрины. То есть задачи делегата - глубоко валидировать возвращенные скрины, и возвращать следующие
 */
internal class CalculationSdkImpl(
    /** gets calculationComponent **/
    private val pScreenDelegate: PScreenSource
) : CalculationSdk {
    // queue, the last is extracted last, items appended to last element, the first element is returned first
    private val queue = WatchingStorage<PScreen>()

    // stores only saved wrapped
    private val savedWrapped = mutableListOf<WrappedPScreen>()

    override fun init(): WrappedPScreen {
        checkDelegateHasNext()
        // append initial pscreen to queue
        queue.init(pScreenDelegate.next())
        return wrapPScreenCurrent(queue.getCurrent())
    }

    override fun validateCurrent(pscreen: PScreen): WrappedPScreen? {
        if (queue.isCurrent(pscreen)) {
            val isGood = pScreenDelegate.validate(pscreen)
            // using delegates method to understand if pscreen is good
            if (isGood == null) {
                queue.commitCurrent(pscreen)
                savedWrapped.add(wrapPScreenCurrent(pscreen))
                return null
            } else {
                // set returned pscreen as current
                queue.replaceCurrentWith(isGood)
                // use delegate to return pscreen with error, replacing it as current in storage
                return wrapPScreenCurrent(queue.getCurrent())
            }
        } else {
            // if it is not current, client is using this api wrong way
            throw IllegalStateException("Given pscreen is not current, wrong parameter for current validation")
        }
    }

    override fun getNextPScreen(): WrappedPScreen {
        checkDelegateHasNext()
        // getting new pscreen from delegate
        val next = pScreenDelegate.next()
        queue.addToBack(next)
        // moving inner cursor to next item
        queue.currentToNext()
        return wrapPScreenCurrent(queue.getCurrent())
    }

    override fun hasNextPScreen(): Boolean {
/*        // если сейчас еще не вернулся активный протоскрин - точно false
        if (queue.isWaitingForCurrent()) {
            return false
        }*/
        // trying to understand that delegate has next screen
        return pScreenDelegate.hasNext()
    }

    override fun getAllValidated(): List<WrappedPScreen> {
        return savedWrapped
    }

    override fun finalResults(): CalculationResults {
        return pScreenDelegate.getResult()
    }

    override fun isFinished(): Boolean {
        return pScreenDelegate.isFinished()
    }

    override fun currentPending(): WrappedPScreen? {
        return if (queue.isWaitingForCurrent()) {
            wrapPScreenCurrent(queue.getCurrentSilently())
        } else null
    }

    private fun wrapPScreenCurrent(pscreen: PScreen): WrappedPScreen {
        // если текущий И последний в сторадже И у источника больше нет - тогда он последний в принципе
        val currentIsLast = queue.isCurrentLast() && !pScreenDelegate.hasNext()
        return WrappedPScreen(pscreen, currentIsLast, queue.currentIndex())
    }

    private fun checkDelegateHasNext(): Boolean {
        if (!pScreenDelegate.hasNext()) {
            throw IllegalStateException("delegate doesnt have any screens prepared at moment")
        } else {
            return true
        }
    }
}

