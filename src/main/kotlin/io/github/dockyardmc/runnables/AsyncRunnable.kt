package io.github.dockyardmc.runnables

class AsyncRunnable(val unit: () -> Unit) {

    var callback: (() -> Unit)? = null

    fun run() {
        val processor = AsyncQueueProcessor()
        val task = AsyncQueueTask("runnable-task", unit)
        val taskCallback = {
            callback?.invoke()
            processor.dispose()
        }
        task.callback = taskCallback
        processor.submit(task)
    }
}