package io.github.dockyardmc.runnables

import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.utils.Disposable
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingUtilities

class AsyncQueueProcessor : Runnable, Disposable {
    private val taskQueue = LinkedTransferQueue<AsyncQueueTask>()
    private val isRunning = AtomicBoolean(true)
    private val thread = Thread(this)

    init {
        thread.start()
    }

    fun submit(task: AsyncQueueTask) {
        ServerMetrics.asyncQueueProcessorTasks++
        taskQueue.offer(task)
    }

    override fun run() {
        while (isRunning.get()) {
            try {
                val task = taskQueue.take()
                task.run()
                ServerMetrics.asyncQueueProcessorTasks--
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    override fun dispose() {
        isRunning.set(false)
        thread.interrupt()
    }
}

class AsyncQueueTask(val name: String, val task: () -> Unit) {
    var callback: (() -> Unit)? = null
    fun run() {
        task.invoke()
        callback?.let { callback ->
            SwingUtilities.invokeLater { callback() }
        }
    }
}