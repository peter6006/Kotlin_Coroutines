package com.example.expert

import com.example.medium.newTopic
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeoutException
import kotlin.random.Random

val countries = listOf("Santander", "Vigo", "Madrid", "Santiago", "Pontevedra", "Ourense")

fun main() {
//    basicChannel()
//    closeChannel()
//    produceChannel()
//    pipelines()
//    bufferChannel()
    exceptions()
    readLine()
}

fun exceptions() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Notify the programmer... $throwable in $coroutineContext")

        if (throwable is ArithmeticException) println("Show Arithmetic message")
        println()
    }

    runBlocking {
        newTopic("Exceptions")
        launch {
            try {
                delay(100)
//                throw Exception()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val globalScope = CoroutineScope(Job() + exceptionHandler)
        globalScope.launch {
            delay(200)
            throw TimeoutException()
        }

        CoroutineScope(Job() + exceptionHandler).launch {
            val result = async {
                delay(500)
                multiLambda(2, 3){
                    if (it > 5) throw ArithmeticException()
                }
            }
            println("Result: ${result.await()}")
        }

        val channel = Channel<String>()
        CoroutineScope(Job()).launch(exceptionHandler) {
            delay(800)
            countries.forEach {
                channel.send(it)
                if (it == "Pontevedra") channel.close()
            }
        }
        channel.consumeEach { println(it) }
    }
}

fun bufferChannel() {
    runBlocking {
        newTopic("Buffer for Channels")
        val time = System.currentTimeMillis()
        val channel = Channel<String>()
        launch {
            countries.forEach {
                delay(100)
                channel.send(it)
            }
            channel.close()
        }

        launch {
            delay(1_000)
            channel.consumeEach { println(it) }
            println("Time: ${System.currentTimeMillis() - time}ms")
        }

        // With buffer
        val bufferTime = System.currentTimeMillis()
        val bufferChannel = Channel<String>(3)
        launch {
            countries.forEach {
                delay(100)
                bufferChannel.send(it)
            }
            bufferChannel.close()
        }

        launch {
            delay(1_000)
            bufferChannel.consumeEach { println(it) }
            println("Buffered Time: ${System.currentTimeMillis() - bufferTime}ms")
        }
    }
}

fun pipelines() {
    runBlocking {
        newTopic("Pipelines")
        val citiesChannel = produceCities()
        val foodChannel = produceFoods(citiesChannel)
        foodChannel.consumeEach { println(it) }
        citiesChannel.cancel()
        foodChannel.cancel()
        println("All have 5 starts")
    }
}

fun produceChannel() {
    runBlocking {
        newTopic("Channels and producer-consumer design")
        val names = produceCities()
        names.consumeEach { println(it) }
    }
}

fun closeChannel() {
    runBlocking {
        newTopic("Close Channel")
        val channel = Channel<String>()
        launch {
            countries.forEach {
                channel.send(it)
                // Cancels the channel in the middle of the execution, triggers an Exception
//                if (it == "Pontevedra") channel.close()

                // Another way of closing the channel with no Exception
                if (it == "Pontevedra") {
                    channel.close()
                    return@launch
                }
            }
            // When countries does not have more data, closes the channel, no Exception raised
//            channel.close()
        }

        // Most effective way of consuming a channel
        while (!channel.isClosedForReceive) {
            println(channel.receive())
        }

        // Another way to consume the results sent
//        channel.consumeEach { println(it) }
    }
}

fun basicChannel() {
    runBlocking {
        newTopic("Basic Channel")
        val channel = Channel<String>()
        launch {
            countries.forEach {
                // Sends the data to the channel
                channel.send(it)
            }
        }

        // Basic way
//        repeat(6){
//            // Receives the data in the channel
//            println(channel.receive())
//        }
        // Endless loop
        for (v in channel) {
            println(v)
        }
    }
}
