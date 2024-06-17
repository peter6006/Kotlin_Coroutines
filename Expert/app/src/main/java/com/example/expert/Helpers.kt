package com.example.expert

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay

fun CoroutineScope.produceCities(): ReceiveChannel<String> = produce {
    countries.forEach {
        send(it)
    }
}

fun CoroutineScope.produceFoods(cities: ReceiveChannel<String>): ReceiveChannel<String> = produce {
    for (city in cities) {
        val food = getFoodByCity(city)
        send("$food from $city")
    }
}

suspend fun getFoodByCity(city: String): String {
    delay(300)
    return when (city) {
        "Santander" -> "Kebab"
        "Vigo" -> "Arepas"
        "Madrid" -> "Ensaimadas"
        "Santiago" -> "Tartas"
        "Pontevedra" -> "Marisco"
        "Ourense" -> "Callos"
        else -> "No data"
    }
}

fun multiLambda(x: Int, y: Int, callback: (result: Int) -> Unit) {
    // Lambda Multiplication
    callback(x * y)
}
