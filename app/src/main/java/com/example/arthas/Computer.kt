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
    onJobsCreated: (List<Job>) -> Unit,
    onSuccess: () -> Unit
): Job {
    return scope.launch(dispatcher) {
        onLoadingChange(true)
        try {
            val jobs = mutableListOf<Job>()
            onProgress(0f)

            val coroutineStart = if (isDelayedStart) CoroutineStart.LAZY else CoroutineStart.DEFAULT

            repeat(countOfCoroutines) {
                val job = launch(Job(coroutineContext[Job]), start = coroutineStart) {
                    val delayTime = Random.nextLong(MIN_DELAY_TIME, MAX_DELAY_TIME)
                    delay(delayTime)

                    if (delayTime >= EXCEPTION_DELAY_TIME && Random.nextInt(1, 11) <= 3) {
                        when (Random.nextInt(1, 4)) {
                            1 -> throw ShowToastException("Toast from coroutine")
                            2 -> throw ShowSnackbarException("Snackbar from coroutine")
                            3 -> throw ResetSettingsException("Reset settings from coroutine")
                        }
                    }
                }
                jobs.add(job)
            }

            onJobsCreated(jobs)

            if (isDelayedStart) {
                jobs.forEach { it.start() }
            }

            var completedJobs = 0
            jobs.forEach { job ->
                job.invokeOnCompletion { throwable ->
                    if (throwable == null) {
                        scope.launch {
                            completedJobs++
                            onProgress(completedJobs.toFloat() / countOfCoroutines.toFloat())
                        }
                    }
                    // Exceptions will propagate up and be handled by the CoroutineExceptionHandler
                }
            }

            if (isSequentially) {
                jobs.forEach { it.join() }
            } else {
                jobs.joinAll()
            }
            onSuccess()
        } finally {
            onLoadingChange(false)
        }
    }
}