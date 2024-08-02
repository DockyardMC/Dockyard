package io.github.dockyardmc.runnables

import kotlinx.coroutines.*
class AsyncRunnable(val unit: () -> Unit) {

    var callback: (() -> Unit)? = null

    fun run() {
        val processor = AsyncQueueProcessor()
        val task = AsyncQueueTask("runnable-task", unit)
        val taskCallback = {
            callback?.invoke()
            processor.shutdown()
        }
        task.callback = taskCallback
        processor.submit(task)
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun runAsync(unit: () -> Unit): Deferred<Unit> {
    return GlobalScope.async {
        unit.invoke()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun Deferred<Unit>.later(callback: () -> Unit) {
    GlobalScope.launch {
        this@later.await()
        callback.invoke()
    }
}