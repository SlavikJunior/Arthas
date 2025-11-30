package com.example.arthas

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
object Computer {

    private const val MIN_DELAY_TIME = 1000L
    private const val MAX_DELAY_TIME = 10000L

    fun computation(
        countOfCoroutines: Int,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        isSequentially: Boolean
    ) {
        if (isSequentially) {
            repeat(countOfCoroutines) { i ->
                val job = scope.launch(dispatcher) {
                    randomDelay()
                }
            }
        }

    }

    private suspend fun randomDelay() =
        delay(Random.nextLong(MIN_DELAY_TIME, MAX_DELAY_TIME))
}