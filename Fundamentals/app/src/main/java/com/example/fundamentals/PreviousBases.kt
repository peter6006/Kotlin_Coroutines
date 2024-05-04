package com.example.fundamentals

fun main() {
    lambda()
}

fun lambda() {
    println(multi(2, 3))

    println(multiLambda(3, 4) {result ->
        println(result)
    })
}

fun multiLambda(x: Int, y: Int, callback: (result: Int) -> Unit) {
    callback(x * y)
}

fun multi(x: Int, y: Int): Int {
    return x * y
}
