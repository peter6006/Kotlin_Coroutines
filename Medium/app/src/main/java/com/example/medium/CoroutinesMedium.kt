package com.example.medium

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.random.Random

/*
 * Dispatchers helps us to define where to execute the threads to optimize resources for specific tasks:
 *      IO: Input and Output, ideal for local and remote SQL access, also read and write files or a task with long duration.
 *      Unconfined: Ideal for when you do not need data communication between threads. Less common.
 *      Main: Only available in Android environment, connected to the main UI Thread, used for quick tasks or to update the view.
 *      Default: Recommended for tasks with high CPU load or long tasks.
 *
 * We can create Nested Threads that starts all together and finalize when they are ready, they do not wait for the other tasks to end.
 * They can also be cancelled, either the parent or their child.
 *
 * With Context allows us to use different context inside a defined coroutine without changing the context of the father.
 *
 * Sequence is a container, its a collection that is in charge of returning values step by step. It executes the process by each element when its requested instead of all together in the same action.
 * Yield is used to generate a final value for the consumer.
 *
 * Flow is used in those cases when there is an Asynchronous code that returns multiple values (instead of 1). Flow will return a sequence of data continuously.
 *
 */
fun main() {
//    dispatchers()
//    nested()
//    changeWithContext()
//    sequences()
    basicFlow()
}

fun basicFlow() {
    newTopic("Basic Flow")
    runBlocking {
        launch {
            getDataByFlow().collect { println(it) }
        }
        launch {
            (1..50).forEach {
                delay(someTime() / 10)
                println("Other Task...")
            }
        }
    }
}

// No need for this function to be Suspend as that behaviour is controlled by 'collect'
fun getDataByFlow(): Flow<Float> {
    return flow {
        (1..5).forEach {
            println("Processing something...")
            delay(someTime())
            emit(20 + it + Random.nextFloat())
        }
    }
}

fun sequences() {
    newTopic("Sequences")
    getDataBySeq().forEach { println("$it") }
}

fun getDataBySeq(): Sequence<Float> {
    return sequence {
        (1..5).forEach {
            println("Processing something...")
            Thread.sleep(someTime())
            yield(20 + it + Random.nextFloat())
        }
    }
}

fun changeWithContext() {
    runBlocking {
        newTopic("With Context")
        startMsg()

        withContext(newSingleThreadContext("Coroutine Course")) {
            startMsg()
            delay(someTime())
            println("Coroutine Course")
            endMsg()
        }

        withContext(Dispatchers.IO) {
            startMsg()
            delay(someTime())
            println("Server request")
            endMsg()
        }

        endMsg()
    }
}

fun nested() {
    runBlocking {
        newTopic("Nested")
        val job = launch {
            startMsg()

            launch {
                startMsg()
                delay(someTime())
                println("Other task")
                endMsg()
            }

            val subJob = launch(Dispatchers.IO) {
                startMsg()

                launch(newSingleThreadContext("Coroutines Course")) {
                    startMsg()
                    println("Task Coroutine")
                    endMsg()
                }

                delay(someTime())
                println("Server Task")
                endMsg()
            }

            delay(someTime() / 4)
            subJob.cancel()
            println("IO task cancelled")

            var sum = 0
            (1..100).forEach {
                sum += it
                delay(someTime() / 100)
            }
            println("Sum = $sum")

            endMsg()
        }

        delay(someTime() / 2)
        job.cancel()
        println("Job cancelled")
    }
}

fun dispatchers() {
    runBlocking {
        newTopic("Dispatchers")
        launch {
            startMsg()
            println("None")
            endMsg()
        }
        launch(Dispatchers.IO) {
            startMsg()
            println("IO")
            endMsg()
        }
        launch(Dispatchers.Unconfined) {
            startMsg()
            println("Unconfined")
            endMsg()
        }
        // Only works in Android
//        launch(Dispatchers.Main) {
//            startMsg()
//            println("Main")
//            endMsg()
//        }
        launch(Dispatchers.Default) {
            startMsg()
            println("Default")
            endMsg()
        }
        launch(newSingleThreadContext("CoroutineCourse")) {
            startMsg()
            println("Custom Dispatcher")
            endMsg()
        }
        newSingleThreadContext("CoroutineCourseOther").use { myContext ->
            launch(Dispatchers.Default) {
                startMsg()
                println("Other Custom Dispatcher")
                endMsg()
            }
        }
    }
}
