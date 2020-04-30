package com.reducetechnologies.command_infrastructure

abstract class PScreenSource : Iterator<WrappedPScreen>{
    protected abstract val preparedStack: MutableList<PScreen>

    override fun hasNext(): Boolean {
        return preparedStack.isNotEmpty()
    }

    override fun next(): WrappedPScreen {
        val toRet = WrappedPScreen(
            pScreen = preparedStack[0],
            isLast = preparedStack.size == 1
        )
        preparedStack.removeAt(0)
        return toRet
    }

    abstract fun isNextLast() : Boolean
    abstract fun validate(pScreen: PScreen) : WrappedPScreen?
}