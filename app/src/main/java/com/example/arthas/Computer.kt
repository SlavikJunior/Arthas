package com.example.arthas

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val MIN_DELAY_TIME = 1000L
private const val MAX_DELAY_TIME = 10000L
private const val EXCEPTION_DELAY_TIME = 7000L

fun computation(
    countOfCoroutines: Int,
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    isSequentially: Boolean,
    isDelayedStart: Boolean,
    onProgress: (Float) -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    onError: (Throwable) -> Unit,
): Job {
    return scope.launch(dispatcher) {
        val jobs = mutableListOf<Job>()

        onLoadingChange(true)
        onProgress(0f)

        val coroutineStart = if (isDelayedStart) CoroutineStart.LAZY else CoroutineStart.DEFAULT

        repeat(countOfCoroutines) {
            val job = launch(dispatcher + Job(coroutineContext[Job]), start = coroutineStart) {
                val delayTime = Random.nextLong(MIN_DELAY_TIME, MAX_DELAY_TIME)
                delay(delayTime)

                if (delayTime >= EXCEPTION_DELAY_TIME && Random.nextInt(1, 11) <= 3) {
                    when (Random.nextInt(1, 4)) {
                        1 -> onError(ShowToastException())
                        2 -> onError(ShowSnackbarException())
                        3 -> onError(ResetSettingsException())
                    }
                }
            }
            jobs.add(job)
        }

        if (isDelayedStart) {
            jobs.forEach { it.start() }
        }

        var completedJobs = 0
        jobs.forEach { job ->
            job.invokeOnCompletion { 
                scope.launch {
                    completedJobs++
                    onProgress(completedJobs.toFloat() / countOfCoroutines.toFloat())
                }
            }
        }

        if (isSequentially) {
            jobs.forEach { it.join() }
        } else {
            jobs.joinAll()
        }

        onLoadingChange(false)
    }
}