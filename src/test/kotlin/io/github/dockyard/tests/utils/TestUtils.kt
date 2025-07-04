package io.github.dockyard.tests.utils

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun <T> waitUntilFuture(future: CompletableFuture<T>, timeout: Duration = 5.seconds) {
    val latch = CountDownLatch(1)
    future.whenComplete { _, _ ->
        latch.countDown()
    }
    try {
        if (!latch.await(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)) {
            throw TimeoutException("CompletableFuture did not complete within $timeout")
        }
        if (future.isCompletedExceptionally) {
            throw IllegalStateException("CompletableFuture completed exceptionally: ${future.exceptionNow()}.")
        }
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        throw IllegalStateException("Thread interrupted while waiting for CompletableFuture.", e)
    } catch (e: TimeoutException) {
        throw IllegalStateException(e.message, e)
    }
}