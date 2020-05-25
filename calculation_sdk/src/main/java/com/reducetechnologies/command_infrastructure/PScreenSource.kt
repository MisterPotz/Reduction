package com.reducetechnologies.command_infrastructure

abstract class PScreenSource : Iterator<PScreen> {
    protected abstract val preparedStack: MutableList<PScreen>
    protected var isOngoing = true
    override fun hasNext(): Boolean {
        return preparedStack.isNotEmpty() || isOngoing
    }

    override fun next(): PScreen {
        val toRet = preparedStack[0]
            /*isLast = preparedStack.size == 1,
            index = */
        preparedStack.removeAt(0)
        return toRet
    }

    abstract fun validate(pScreen: PScreen) : PScreen?

    abstract fun getResult() : CalculationResults

    abstract fun isFinished() : Boolean
}