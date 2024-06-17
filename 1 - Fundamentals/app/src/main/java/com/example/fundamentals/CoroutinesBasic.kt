package com.example.fundamentals

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
* GlobalScope allows the Coroutine to stay alive as long as the execution also stays alive
* RunBlocking blocks the main Thread until it finish. Only to be used by Test or debug
* Launch is used for tasks that do not need a return
* Async is used for when we need a result as a return
* Job is a task that can be cancelled
* */
/*
* Job LifeCycle:
*
* New -> Active -> Completing -----
*              \       |           |
*               \      V           V
*                -> Cancelling -> Finish -> Completed
*                                 |
*                                 V
*                             Cancelled
*/
fun main(){
//    globalScope()
//    suspendFun()
    newTopic("Coroutines Constructors")
//    cRunBlocking()
//    cLaunch()
//    cAsync()
//    job()
//    deferred()
//    cProduce()

    readLine()
}

fun cProduce() = runBlocking {
    newTopic("Produce")
    val names = produceNames()
    names.consumeEach { println(it) }
}

fun CoroutineScope.produceNames(): ReceiveChannel<String> = produce {
    (1..5).forEach { send("name$it") }
}

fun deferred() {
    runBlocking {
        newTopic("Deferred")
        val deferred = async {
            startMsg()
            delay(someTime())
            println("Running Deferred...")
            endMsg()
            multi(5, 4)
        }
        println("Deferred: $deferred")
        println("Value Deferred.await: ${deferred.await()}")

        val result = async {
            multi(3, 3)
        }.await()
        println("Result: $result")
    }
}

fun job() {
    runBlocking {
        newTopic("Job")
        val job = launch {
            startMsg()
            delay(2_100)
            println("Running Job...")
            endMsg()
        }
        println("Job: $job")

        println("Job isActive: ${job.isActive}")
        println("Job isCancelled: ${job.isCancelled}")
        println("Job isCompleted: ${job.isCompleted}")

        delay(someTime())
        println("Task Cancelled")
        job.cancel()

        println("Job isActive: ${job.isActive}")
        println("Job isCancelled: ${job.isCancelled}")
        println("Job isCompleted: ${job.isCompleted}")
    }
}

fun cAsync() {
    runBlocking {
        newTopic("Async")
        val res = async {
            startMsg()
            delay(someTime())
            println("Running Async...")
            endMsg()
            1
        }
        println("Result: ${res.await()}")
    }
}

fun cLaunch() {
    runBlocking {
        newTopic("Launch")
        launch {
            startMsg()
            delay(someTime())
            println("Launching...")
            endMsg()
        }
    }
}

fun cRunBlocking() {
    newTopic("RunBlocking")
    runBlocking {
        startMsg()
        delay(someTime())
        println("Running Blocking...")
        endMsg()
    }
}

fun suspendFun() {
    newTopic("Suspend")
    Thread.sleep(someTime())
//    delay(someTime())
    GlobalScope.launch { delay(someTime()) }
}

fun globalScope() {
    newTopic("Global Scope")
    GlobalScope.launch {
        startMsg()
        delay(someTime())
        println("My coroutine")
        endMsg()
    }
}

fun startMsg() {
    println("Started Coroutine -${Thread.currentThread().name}-")
}

fun endMsg() {
    println("Coroutine -${Thread.currentThread().name}- Finished")
}
