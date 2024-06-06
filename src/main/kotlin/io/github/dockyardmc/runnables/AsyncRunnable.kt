package io.github.dockyardmc.runnables

import log

class AsyncRunnable(val code: () -> Unit) {

    var runAfterFinished: (() -> Unit)? = null
    val asyncThread = Thread("AsyncRunnableThread")
    val currentThread = Thread.currentThread()

    fun start() {
        asyncThread.run {
            code.invoke()
            runAfterFinished()
        }
        asyncThread.start()
    }

    fun runAfterFinished() {
        if(runAfterFinished == null) return
        currentThread.run {
            runAfterFinished!!.invoke()
        }
    }

    fun stop() {
        asyncThread.stop()
    }
}