package com.reducetechnologies.command_infrastructure

import java.lang.IllegalStateException

internal class CalculationSdkImpl(
    /** gets calculationComponent **/
) : CalculationSdk {
    // queue, the last is extracted last, items appended to last element, the first element is returned first
    private val queue = WatchingStorage<PScreen>()

    override fun init(): PScreen {
        // append initial pscreen to queue
        queue.init(PScreen())
        return queue.getCurrent()
    }

    override fun validateCurrent(pscreen: PScreen): PScreen? {
        if (queue.isCurrent(pscreen)) {
            // using delegates method to understand if pscreen is good
            if (true) {
                queue.commitCurrent(pscreen)
                return null
            } else {
                // use delegate to return pscreen with error, replacing it as current in storage
                return PScreen()
            }
        } else {
            // if it is not current, client is using this api wrong way
            throw IllegalStateException("Given pscreen is not current, wrong parameter for current validation")
        }
    }

    override fun getNextPScreen(): PScreen {
        // moving inner cursor to next item
        queue.currentToNext()
        return queue.getCurrent()
    }

    override fun hasNextPScreen(): Boolean {
        return queue.hasNext()
    }
}

