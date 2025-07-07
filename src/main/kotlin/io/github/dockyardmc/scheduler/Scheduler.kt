package io.github.dockyardmc.scheduler

import io.github.dockyardmc.extentions.round
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.debug
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.time.Duration

abstract class Scheduler(val name: String) : Disposable {

    var ticks: Long = 0
    var mspt: Double = 0.0

    val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor {
        val thread = Thread(it, name)
        thread.isDaemon = true
        thread
    }

    private val scheduledTasks: MutableMap<Long, MutableList<SchedulerTask>> =
        mutableMapOf() // scheduler tick time to task
    private val scheduledTasksAsync: MutableMap<Long, MutableList<AsyncSchedulerTask<*>>> =
        mutableMapOf() // scheduler tick time to task
    private val repeatingTasks: MutableMap<Long, MutableList<SchedulerTask>> =
        mutableMapOf() // scheduler tick % interval to task
    private val repeatingTasksAsync: MutableMap<Long, MutableList<AsyncSchedulerTask<*>>> =
        mutableMapOf() // scheduler tick % interval to task

    val taskSize: Int get() = scheduledTasks.size + scheduledTasksAsync.size + repeatingTasks.size + repeatingTasksAsync.size

    protected val averages = mutableListOf<Long>(50)
    protected var timeSinceLastTick = Instant.now()

    protected open fun updateMSPT() {
        val diff = Instant.now().toEpochMilli() - timeSinceLastTick.toEpochMilli()
        averages.add(diff)
        timeSinceLastTick = Instant.now()
        mspt = averages.average().round(1)
        if (mspt < 50) mspt = 50.0
        if(ticks % 20 == 0L) averages.clear()
    }

    open fun tick() {
        ticks++
        handleTickTasks()
        updateMSPT()
    }

    private fun handleTickTasks() {
        handleRepeatingTasks(repeatingTasks)
        handleRepeatingTasksAsync(repeatingTasksAsync)

        val tickStartTasks = scheduledTasks[ticks]
        if (tickStartTasks != null) handleTickQueue(scheduledTasks)
        handleTickQueueAsync(scheduledTasksAsync)
    }

    private fun handleRepeatingTasks(tasks: MutableMap<Long, MutableList<SchedulerTask>>) {
        tasks.forEach intervalLoop@{ (interval, tasks) ->
            if (ticks % interval == 0L) {
                tasks.toList().forEach taskLoop@{ task ->
                    if (task.cancelled) {
                        tasks.remove(task)
                        return@taskLoop
                    }
                    executorService.submit {
                        task.run(ticks)
                    }
                }
            }
        }
    }

    private fun handleRepeatingTasksAsync(tasks: MutableMap<Long, MutableList<AsyncSchedulerTask<*>>>) {
        tasks.forEach intervalLoop@{ (interval, tasks) ->
            if (ticks % interval == 0L) {
                tasks.toList().forEach taskLoop@{ task ->
                    if (task.cancelled) {
                        tasks.remove(task)
                        return@taskLoop
                    }
                    runAsync {
                        task.run(ticks)
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
        if (!task.cancelled) task.run(ticks)
        scheduledTasks[ticks]?.remove(task)
    }

    private fun handleTaskAsync(task: AsyncSchedulerTask<*>) {
        if (!task.cancelled) runAsync { task.run(ticks) }
        scheduledTasksAsync[ticks]?.remove(task)
    }

    open fun addTask(task: SchedulerTask, time: Duration) {
        val timeInTicks = (time.inWholeMilliseconds / 50)
        val ticks = if (task.type == SchedulerTask.Type.TICK) timeInTicks + ticks else timeInTicks
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

    open fun runLater(duration: Duration, unit: (SchedulerTask) -> Unit): SchedulerTask {
        val task = SchedulerTask(unit, SchedulerTask.Type.TICK)
        addTask(task, duration)

        return task
    }

    open fun run(unit: () -> Unit) {
        try {
            executorService.submit(unit)
        } catch (ex: Exception) {
            throw ex
        }
    }

    @JvmName("runLaterAsyncTyped")
    fun <T> runLaterAsync(duration: Duration, unit: (AsyncSchedulerTask<T>) -> T): CompletableFuture<T> {
        val ticks = (duration.inWholeMilliseconds / 50) + ticks
        val task = AsyncSchedulerTask(unit, SchedulerTask.Type.TICK)

        var list = scheduledTasksAsync[ticks]
        if (list == null) {
            scheduledTasksAsync[ticks] = mutableListOf()
            list = scheduledTasksAsync[ticks]!!
        }

        list.add(task)
        return task.future
    }

    open fun runRepeatingAsync(interval: Duration, unit: (AsyncSchedulerTask<Unit>) -> Unit): AsyncSchedulerTask<Unit> {
        val task = AsyncSchedulerTask(unit, SchedulerTask.Type.REPEATING)
        var list = repeatingTasksAsync[ticks]

        if (list == null) {
            list = mutableListOf()
            repeatingTasksAsync[ticks] = list
        }

        list.add(task)
        return task
    }

    open fun runRepeating(interval: Duration, unit: (SchedulerTask) -> Unit): SchedulerTask {
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
        executorService.shutdown()
        scheduledTasks.clear()
        repeatingTasks.clear()
    }

    fun repeat(times: Int, delay: Duration, func: (SchedulerTask) -> Unit): SchedulerTask {
        val task = SchedulerTask(func, SchedulerTask.Type.REPEATING, "repeat")

        var i = 0
        val repeatingScheduler = runRepeating(delay) { localTask ->
            if(task.cancelled) localTask.cancel()
            i++
            func.invoke(task)
            if(i == times) localTask.cancel()
        }
        return task
    }
}