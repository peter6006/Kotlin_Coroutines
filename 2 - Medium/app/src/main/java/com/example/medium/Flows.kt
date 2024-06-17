package com.example.medium

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import kotlin.system.measureTimeMillis


/*
 * In a Flow environment, the function is not called until the 'collect' is not reached. While 'collect' is not called, the function is suspended.
 * Several Operators can be concatenated.
 */
fun main() {
//    coldFlow()
//    cancelFlow()
//    flowOperators()
//    terminalFlowOperators()
//    bufferFlow()
//    conflationFlow()
//    multiFlow()
//    flatFlows()
//    flowExceptions()
    completions()
}

fun completions() {
    runBlocking {
        newTopic("End of a Flow (onCompletion)")
        getCitiesFlow()
            .onCompletion { println("Finished!") }
//            .collect { println(it) }
        println()

        getMatchResultsFlow()
            .onCompletion { println("Show stats...") }
            .catch { emit("Error: $this") }
//            .collect { println(it) }

        newTopic("Cancel Flow")
        getDataByFlowStatic()
            .onCompletion { println("Finished!!") }
            .cancellable()
            .collect {
                if (it > 22.5f) cancel()
                println(it)
            }
    }
}

fun flowExceptions() {
    runBlocking {
        newTopic("Error Control")
        newTopic("Try/Catch")
        try {
            getMatchResultsFlow()
                .collect {
                    println(it)
                    if (it.contains("2")) throw Exception("2 goals are not allowed")
                }
        } catch (e: Exception) {
            // Flow is also cancelled
            e.printStackTrace()
        }

        newTopic("Transparency")
        getMatchResultsFlow()
            .catch {
                emit("Error: $this")
            }
            .collect {
                println(it)
                if (!it.contains("-")) println("Notify the programmer")
            }
    }
}

fun flatFlows() {
    runBlocking {
        newTopic("Flatting Flows Concat")
        getCitiesFlow()
            .flatMapConcat { cities -> // Flow<Flow<Any>>
                getDataToFlatFlow(cities)
            }
            .map { setFormat(it) }
            .collect { println(it) }


        newTopic("Flatting Flows Merge")
        getCitiesFlow()
            .flatMapMerge { cities -> // Flow<Flow<Any>>
                getDataToFlatFlow(cities)
            }
            .map { setFormat(it) }
            .collect { println(it) }
    }
}

fun multiFlow() {
    runBlocking {
        newTopic("Zip & Combine")
        getDataByFlowStatic()
            .map {
                setFormat(it)
            }
            .combine(getMatchResultsFlow()) { degrees, result -> // Does not wait and follows the biggest Flow
//            .zip(getMatchResultsFlow()) { degrees, result -> // Flow as big as the smallest one
                "$result with $degrees"
            }
            .collect { println(it) }
    }
}

fun conflationFlow() {
    runBlocking {
        newTopic("Fusion")
        val time = measureTimeMillis {
            getMatchResultsFlow()
//                .conflate() // 3143ms
//                .buffer() // 5310ms
                .collect { // 8063ms
//                .collectLatest{ // 3139ms
                    delay(100)
                    println(it)
                }
        }
        println("Time: ${time}ms")
    }
}

fun bufferFlow() {
    runBlocking {
        newTopic("Buffer Flow")
        val time = measureTimeMillis {
            getDataByFlowStatic()
                .map { setFormat(it) }
                // Allows to reduce the time of execution maintaining the order of execution
                .buffer()
                .collect {
                    delay(500)
                    println(it)
                }
        }
        println("Time: ${time}ms")
    }
}

fun terminalFlowOperators() {
    runBlocking {
        newTopic("Flow Terminal Operators")
        newTopic("List")
        val list = getDataByFlow()
        // Converts the Flow into a List, meaning that the val 'list' will contain all the data
//            .toList()
        println("List: $list")

        newTopic("Single")
        val single = getDataByFlow()
        // Single can only work when the Flow returns 1 value, that's why we use the 'Take' operator
//            .take(1)
//            .single()
        println("Single: $single")

        newTopic("First")
        val first = getDataByFlow()
//            .first()
        println("First: $first")

        newTopic("Last")
        val last = getDataByFlow()
//            .last()
        println("Last: $last")

        newTopic("Reduce")
        val saving = getDataByFlow()
            // Used to sum all values of a Flow
            .reduce { accumulator, value ->
                println("Accumulator: $accumulator")
                println("Value: $value")
                println("Curret savings: ${accumulator + value}")
                accumulator + value
            }
        println("Savings: $saving")

        newTopic("Fold")
        val lastSaving = saving
        val totalSaving = getDataByFlow()
            // Same as Reduce, but with an init value
            .fold(lastSaving) { accumulator, value ->
                println("Accumulator: $accumulator")
                println("Value: $value")
                println("Curret savings: ${accumulator + value}")
                accumulator + value
            }
        println("Total Savings: $totalSaving")
    }
}

fun flowOperators() {
    runBlocking {
        newTopic("Flow Intermediate Operators")
        newTopic("Map")
        getDataByFlow()
            .map {
                // Process the data before emitting it to the collect function.
                // Only the second data-processor is taken into account
                setFormat(it)
                setFormat(convertCelsToFahr(it), "F")
            }
//            .collect { println(it) } // Meanwhile this is commented, nothing will be executed

        newTopic("Filter")
        getDataByFlow()
            .filter {
                // Filters the data before continuing to work with it, similar to an IF.
                // Map and Filter can be switched, but the output of the Map will be the Input of the Filter, changing values or types in the process.
                it < 23
            }
            .map { setFormat(it) }
//            .collect{ println(it) } // Meanwhile this is commented, nothing will be executed

        newTopic("Transform")
        getDataByFlow()
            .transform {
                // Allows to emit several values to the collect function.
                emit(setFormat(it))
                emit(setFormat(convertCelsToFahr(it), "F"))
            }
//            .collect { println(it) } // Meanwhile this is commented, nothing will be executed

        newTopic("Take")
        getDataByFlow()
            // Limits the size of the Flow, will only emit the next X values (3 in this case)
            .take(3)
            .map { setFormat(it) }
            .collect { println(it) }
    }
}

fun cancelFlow() {
    runBlocking {
        newTopic("Cancel Flow")
        val job = launch {
            getDataByFlow().collect { println(it) }
        }
        delay(someTime() * 2)
        job.cancel()
    }
}

fun coldFlow() {
    newTopic("Flow are Cold")
    runBlocking {
        val dataflow = getDataByFlow()
        println("Waiting...")
        delay(someTime())
        dataflow.collect { println(it) }
    }
}
