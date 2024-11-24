package io.github.dockyardmc.scheduler

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.utils.Disposable
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.time.Duration

@OptIn(InternalCoroutinesApi::class)
class Scheduler(val name: String) : Disposable {

    companion object {
        val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor {
            val thread = Thread(it)
            thread.isDaemon = true
            thread
        }
    }

    fun makeGlobal(): Scheduler {
        SchedulerManager.registerGlobal(this)
        return this
    }

    private val scheduledTasks: MutableMap<Long, MutableList<SchedulerTask>> = mutableMapOf() // scheduler tick time to task
    private val scheduledTasksAsync: MutableMap<Long, MutableList<AsyncSchedulerTask<*>>> = mutableMapOf() // scheduler tick time to task
    private val repeatingTasks: MutableMap<Long, MutableList<SchedulerTask>> = mutableMapOf() // scheduler tick % interval to task
    private val repeatingTasksAsync: MutableMap<Long, MutableList<AsyncSchedulerTask<*>>> = mutableMapOf() // scheduler tick % interval to task

    private var ticks: Long = 0
    private var serverTicks: Long = 0

    fun tick(serverTicks: Int) {
        this.serverTicks = serverTicks.toLong()
        ticks++
        val tickStartTasks = scheduledTasks[ticks]
        if (tickStartTasks != null) handleTickQueue(scheduledTasks)
        handleRepeatingTasks(repeatingTasks)
        tickAsync()
    }

    fun tickAsync() {
        handleTickQueueAsync(scheduledTasksAsync)
        handleRepeatingTasksAsync(repeatingTasksAsync)
    }

    private fun handleRepeatingTasks(tasks: MutableMap<Long, MutableList<SchedulerTask>>) {
        tasks.forEach intervalLoop@{ (interval, tasks) ->
            if (ticks % interval == 0L) {
                tasks.toList().forEach taskLoop@{
                    if (it.cancelled) {
                        tasks.remove(it)
                        return@taskLoop
                    }
                    executorService.submit {
                        it.run(serverTicks, ticks)
                    }
                }
            }
        }
    }

    private fun handleRepeatingTasksAsync(tasks: MutableMap<Long, MutableList<AsyncSchedulerTask<*>>>) {
        tasks.forEach intervalLoop@{ (interval, tasks) ->
            if (ticks % interval == 0L) {
                tasks.toList().forEach taskLoop@{
                    if (it.cancelled) {
                        tasks.remove(it)
                        return@taskLoop
                    }
                    runAsync {
                        it.run()
                    }
                }
            }
        }
    }

    private fun handleTickQueue(queue: MutableMap<Long, MutableList<SchedulerTask>>) {
        val currentTasks = queue[ticks] ?: return
        synchronized(currentTasks) {
            currentTasks.toList().forEach { task ->
                handleTask(task)
            }
            if (currentTasks.isEmpty()) queue.remove(ticks)
        }
    }

    private fun handleTickQueueAsync(queue: MutableMap<Long, MutableList<AsyncSchedulerTask<*>>>) {
        val currentTasks = queue[ticks] ?: return
        synchronized(currentTasks) {
            currentTasks.toList().forEach { task ->
                handleTaskAsync(task)
            }
            if (currentTasks.isEmpty()) queue.remove(ticks)
        }
    }

    private fun handleTask(task: SchedulerTask) {
        if (!task.cancelled) task.run(serverTicks, ticks)
        scheduledTasks[ticks]?.remove(task)
    }

    private fun handleTaskAsync(task: AsyncSchedulerTask<*>) {
        if (!task.cancelled) runAsync { task.run() }
        scheduledTasksAsync[ticks]?.remove(task)
    }

    fun addTask(task: SchedulerTask, time: Duration) {
        val timeInTicks = (time.inWholeMilliseconds / 50)
        val ticks = if(task.type == SchedulerTask.Type.TICK) timeInTicks + ticks else timeInTicks
        when (task.type) {
            SchedulerTask.Type.IMMEDIATE -> handleTask(task)
            SchedulerTask.Type.TICK -> {
                var list = scheduledTasks[ticks]
                if (list == null) {
                    scheduledTasks[ticks] = mutableListOf()
                    list = scheduledTasks[ticks]!!
                }

                list.add(task)
            }

            SchedulerTask.Type.REPEATING -> {
                var list = repeatingTasks[ticks]
                if (list == null) {
                    repeatingTasks[ticks] = mutableListOf()
                    list = repeatingTasks[ticks]!!
                }
                list.add(task)
            }
        }
    }

    fun runLater(duration: Duration, unit: () -> Unit): SchedulerTask {
        val task = SchedulerTask(unit, SchedulerTask.Type.TICK)
        addTask(task, duration)

        return task
    }

    fun run(unit: () -> Unit) {
        executorService.submit(unit)
    }

    @JvmName("runLaterAsyncTyped")
    fun <T> runLaterAsync(duration: Duration, unit: () -> T): CompletableFuture<T> {
        val ticks = (duration.inWholeMilliseconds / 50) + ticks
        val task = AsyncSchedulerTask(unit, SchedulerTask.Type.TICK)

        var list = scheduledTasksAsync[ticks]
        if(list == null) {
            scheduledTasksAsync[ticks] = mutableListOf()
            list = scheduledTasksAsync[ticks]!!
        }

        list.add(task)
        return task.future
    }

    fun runRepeatingAsync(interval: Duration, unit: () -> Unit): AsyncSchedulerTask<Unit> {
        val task = AsyncSchedulerTask(unit, SchedulerTask.Type.REPEATING)
        var list = repeatingTasksAsync[ticks]
        if(list == null) {
            repeatingTasksAsync[ticks] = mutableListOf()
            list = scheduledTasksAsync[ticks]!!
        }

        list.add(task)
        return task
    }

    fun runRepeating(interval: Duration, unit: () -> Unit): SchedulerTask {
        val task = SchedulerTask(unit, SchedulerTask.Type.REPEATING)
        addTask(task, interval)
        return task
    }

    @JvmName("runAsyncTyped")
    fun <T> runAsync(unit: () -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        try {
            return CompletableFuture.supplyAsync(unit)
        } catch (ex: Exception) {
            future.completeExceptionally(ex)
        }
        return future
    }

    @JvmName("runAsyncVoid")
    fun runAsync(unit: () -> Unit): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        try {
            return CompletableFuture.supplyAsync<Unit>(unit)
        } catch (ex: Exception) {
            future.completeExceptionally(ex)
        }
        return future
    }

    override fun dispose() {
        if(SchedulerManager.list.contains(this)) SchedulerManager.unregisterGlobal(this)
        executorService.shutdown()
        scheduledTasks.clear()
        repeatingTasks.clear()
    }
}

@JvmName("runLaterAsyncTyped")
fun <T> runLaterAsync(duration: Duration, unit: () -> T): CompletableFuture<T> {
    return DockyardServer.scheduler.runLaterAsync(duration, unit)
}

@JvmName("runLaterAsyncTypedTicks")
fun <T> runLaterAsync(ticks: Int, unit: () -> T): CompletableFuture<T> {
    return DockyardServer.scheduler.runLaterAsync(ticks.ticks, unit)
}

@JvmName("runLaterAsyncVoid")
fun runLaterAsync(duration: Duration, unit: () -> Unit): CompletableFuture<Unit> {
    return DockyardServer.scheduler.runLaterAsync<Unit>(duration, unit)
}

@JvmName("runLaterAsyncVoidTicks")
fun runLaterAsync(ticks: Int, unit: () -> Unit): CompletableFuture<Unit> {
    return DockyardServer.scheduler.runLaterAsync<Unit>(ticks.ticks, unit)
}