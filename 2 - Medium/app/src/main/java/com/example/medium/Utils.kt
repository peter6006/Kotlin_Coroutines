package com.example.medium

import java.util.Locale
import kotlin.random.Random

private const val SEPARATOR = "===================="
fun newTopic(topic: String) {
    println("\n$SEPARATOR $topic $SEPARATOR\n")
}

fun startMsg() {
    println("Started Coroutine -${Thread.currentThread().name}-")
}

fun endMsg() {
    println("Coroutine -${Thread.currentThread().name}- Finished")
}

fun someTime(): Long = Random.nextLong(500, 2_000)

fun setFormat(temp: Float, degree: String = "C"): String = String.format(Locale.getDefault(), "%.1fº$degree", temp)

fun convertCelsToFahr(cels: Float): Float = ((cels * 9) / 5) + 32