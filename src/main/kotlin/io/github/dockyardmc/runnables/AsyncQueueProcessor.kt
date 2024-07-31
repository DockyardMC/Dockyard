package io.github.dockyardmc.runnables

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingUtilities

class AsyncQueueProcessor : Runnable {
    private val taskQueue = LinkedBlockingQueue<AsyncQueueTask>()
    private val isRunning = AtomicBoolean(true)
    private val thread = Thread(this)

    init {
        thread.start()
    }

    fun submit(task: AsyncQueueTask) {
        taskQueue.offer(task)
    }

    override fun run() {
        while (isRunning.get()) {
            try {
                val task = taskQueue.take() // Blocks until a task is available
                task.run()
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    fun shutdown() {
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