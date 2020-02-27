package com.reducetechnologies

import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

fun fib(index: Long): Long {
    when (index) {
        0L, 1L -> return 1
        else -> if (index < 0) throw IllegalArgumentException()
    }
    var a: Long = 1
    var b: Long = 1
    var temp : Long= 0
    var current: Long = 1
    do {
        temp = a + b
        a = b
        b = temp
        current++
    } while (current != index)
    return b
}

fun naiveFib(n : Long) : Long{
    if (n <= 1L)
        return 1L
    else
        return naiveFib(n - 1L) + naiveFib(n-2L)
}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun main(args: Array<String>) {
    newSingleThreadContext("Ctx1").use { ctx1 ->
        newSingleThreadContext("Ctx2").use { ctx2 ->
            runBlocking(ctx1) {
                log("Started in ctx1")
                withContext(ctx2) {
                    log("Working in ctx2")
                }
                log("Back to ctx1")
            }
        }
    }
}

