package com.reducetechnologies.command_infrastructure

import java.lang.IllegalStateException

/**
 * Knows current instance, can get first, can get last, can get current
 */
class WatchingStorage<T>() {
    private val queue: MutableList<T> = mutableListOf()
    private var current: Int = 0
    private var isInit: Boolean = false
    private var currentIsPending = false

    fun addToBack(obj: T) {
        checkIsInit()
        queue.add(obj)
    }

    fun init(obj: T) {
        queue.add(obj)
        isInit = true
        current = 0
    }

    private fun checkIsInit() {
        if (!isInit) {
            throw IllegalStateException("Structure is not yet initialized")
        }
    }

    /**
     * Setting current to be pending - it is given out
     */
    fun getCurrent(): T {
        checkIsInit()
        currentIsPending = true
        return queue[current]!!
    }

    fun isCurrent(obj: T): Boolean {
        checkIsInit()
        if (queue.isEmpty()) {
            return false
        }
        return queue[current] == obj
    }

    /**
     * Current must be commited before giving out next
     */
    fun commitCurrent(obj: T) {
        if (queue[current] == obj) {
            currentIsPending = false
        } else {
            throw IllegalStateException("Committed value is not current")
        }
    }

    fun isWaitingForCurrent() : Boolean {
        return currentIsPending
    }

    private fun checkNotPending() {
        if (isWaitingForCurrent()) {
            throw IllegalStateException("Current is pending, procedure for next cannot be committed")
        }
    }

    /**
     * Allows replacing even if current is dispatched. Then, the dispatching parameter is unset
     */
    fun replaceCurrentWith(obj: T) {
        checkIsInit()
        if (isWaitingForCurrent()) {
            // current is no more dispatched
            currentIsPending = false
        }
        queue[current] = obj
    }

    /**
     * true if next can be returned, false - if can't
     */
    fun currentToNext(): Boolean {
        checkIsInit()
        checkNotPending()
        return hasNext().apply {
            if (this) {
                current += 1
            }
        }
    }

    fun hasNext() : Boolean {
        return current + 1 < queue.size
    }

    fun isInit(): Boolean {
        return isInit
    }
}