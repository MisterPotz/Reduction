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

    override fun init(): PScreen {
        checkDelegateHasNext()
        // append initial pscreen to queue
        queue.init(pScreenDelegate.next())
        return queue.getCurrent()
    }

    override fun validateCurrent(pscreen: PScreen): PScreen? {
        if (queue.isCurrent(pscreen)) {
            val isGood = pScreenDelegate.validate(pscreen)
            // using delegates method to understand if pscreen is good
            if (isGood == null) {
                queue.commitCurrent(pscreen)
                return null
            } else {
                // use delegate to return pscreen with error, replacing it as current in storage
                return isGood
            }
        } else {
            // if it is not current, client is using this api wrong way
            throw IllegalStateException("Given pscreen is not current, wrong parameter for current validation")
        }
    }

    override fun getNextPScreen(): PScreen {
        checkDelegateHasNext()
        // getting new pscreen from delegate
        val next = pScreenDelegate.next()
        queue.addToBack(next)
        // moving inner cursor to next item
        queue.currentToNext()
        return queue.getCurrent()
    }

    override fun hasNextPScreen(): Boolean {
        // если сейчас еще не вернулся активный протоскрин - точно false
        if (queue.isWaitingForCurrent()) {
            return false
        }
        // trying to understand that delegate has next screen
        return pScreenDelegate.hasNext()
    }

    override fun finalResults(): CalculationResults {
        return StubResults()
    }

    private fun checkDelegateHasNext() : Boolean {
        if (!pScreenDelegate.hasNext()) {
            throw IllegalStateException("delegate doesnt have any screens prepared at moment")
        } else {
            return true
        }
    }
}

