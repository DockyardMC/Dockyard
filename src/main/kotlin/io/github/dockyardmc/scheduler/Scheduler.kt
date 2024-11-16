package io.github.dockyardmc.scheduler

import io.github.dockyardmc.utils.Disposable
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration

@OptIn(InternalCoroutinesApi::class)
class Scheduler(val name: String): Disposable {

    companion object {
        val taskCounter: AtomicInteger = AtomicInteger()
        val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor {
            val thread = Thread(it)
            thread.isDaemon = true
            thread
        }
    }

    fun register() {
        SchedulerManager.register(this)
    }

    fun unregister() {
        SchedulerManager.unregister(this)
    }

    private val tickStartScheduledTasks: MutableMap<Long, MutableList<SchedulerTask>> = mutableMapOf() // scheduler tick time to task
    private val tickEndScheduledTasks: MutableMap<Long, MutableList<SchedulerTask>> = mutableMapOf() // scheduler tick time to task
    private val repeatingTasks: MutableMap<Long, MutableList<SchedulerTask>> = mutableMapOf() // scheduler tick % interval to task

    private var ticks: Long = 0
    private var serverTicks: Long = 0

    fun tickStart(serverTicks: Int) {
        this.serverTicks = serverTicks.toLong()
        ticks++
        val tickStartTasks = tickStartScheduledTasks[ticks]
        if(tickStartTasks != null) handleTickQueue(tickStartScheduledTasks)
    }

    fun tickEnd() {
        val tickEndTasks = tickStartScheduledTasks[ticks]
        if(tickEndTasks != null) handleTickQueue(tickEndScheduledTasks)
    }

    private fun handleTickQueue(queue: MutableMap<Long, MutableList<SchedulerTask>>) {
        val currentTasks = queue[ticks]
        if(currentTasks != null) {
            synchronized(currentTasks) {
                currentTasks.toList().forEach { task ->
                    handleTask(task)
                }
                if(currentTasks.isEmpty()) queue.remove(ticks)
            }
        }
    }

    private fun handleTask(task: SchedulerTask) {
        if(!task.cancelled) task.run(serverTicks, ticks)
        tickStartScheduledTasks[ticks]?.remove(task)
    }

    fun addTask(task: SchedulerTask, time: Duration) {
        val ticks = ticks + (time.inWholeMilliseconds / 50)
        when(task.type) {
            SchedulerTask.Type.IMMEDIATE -> handleTask(task)
            SchedulerTask.Type.TICK_START -> {
                var list = tickStartScheduledTasks[ticks]
                if(list == null) {
                    tickStartScheduledTasks[ticks] = mutableListOf()
                    list = tickStartScheduledTasks[ticks]!!
                }

                list.add(task)
            }
            SchedulerTask.Type.TICK_END -> {
                var list = tickEndScheduledTasks[ticks]
                if(list == null) {
                    tickEndScheduledTasks[ticks] = mutableListOf()
                    list = tickEndScheduledTasks[ticks]!!
                }

                list.add(task)
            }
            SchedulerTask.Type.REPEATING_TICK_START -> TODO()
            SchedulerTask.Type.REPEATING_TICK_END -> TODO()
        }
    }

    override fun dispose() {
        unregister()
        executorService.shutdown()
        tickStartScheduledTasks.clear()
        repeatingTasks.clear()
    }
}