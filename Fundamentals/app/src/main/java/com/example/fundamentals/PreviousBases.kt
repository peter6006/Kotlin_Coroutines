package com.example.fundamentals

import kotlin.concurrent.thread
import kotlin.random.Random

fun main() {
    lambda()

    threads()
}

fun threads() {
    println("Thread: ${multiThread(4, 5)}")
    multiThreadLambda(5, 6) {
        println("Thread + Lambda: $it")
    }
}

fun multiThread(x: Int, y: Int): Int {
    /*
    Wrong use of Thread
    For this to work we should create the val Thread and then call .join
    */
    var result = 0

    thread {
        Thread.sleep(someTime())
        result = x * y
    }

    return result
}

fun multiThreadLambda(x: Int, y: Int, callback: (result: Int) -> Unit) {
    // Thread + Lambda Multiplication
    var result = 0

    thread {
        Thread.sleep(someTime())
        result = x * y
        callback(result)
    }
}

fun someTime(): Long = Random.nextLong(500, 2_000)

fun lambda() {
    println(multi(2, 3))

    println(multiLambda(3, 4) { result ->
        println(result)
    })
}

fun multiLambda(x: Int, y: Int, callback: (result: Int) -> Unit) {
    // Lambda Multiplication
    callback(x * y)
}

fun multi(x: Int, y: Int): Int {
    // Normal Multiplication
    return x * y
}
