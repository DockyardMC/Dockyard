package io.github.dockyardmc.runnables

import kotlinx.coroutines.*
import java.util.concurrent.Executors

class AsyncRunnable(
    private val task: suspend () -> Unit
) {
    var callback: (() -> Unit)? = null
    private val executorService = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    fun execute() {
        CoroutineScope(executorService).launch {
            try {
                task()
                withContext(Dispatchers.Default) {
                    callback?.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                executorService.close()
            }
        }
    }
}