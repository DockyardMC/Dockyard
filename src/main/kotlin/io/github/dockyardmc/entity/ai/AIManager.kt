package io.github.dockyardmc.entity.ai

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.WorldTickEvent
import io.github.dockyardmc.events.system.EventFilter
import io.github.dockyardmc.utils.Disposable

class AIManager(val entity: Entity) : Disposable {
    val memory: MutableMap<String, AIMemory<*>> = mutableMapOf()
    private val innerGoals: MutableList<AIGoal> = mutableListOf()
    var currentGoal: AIGoal? = null
    var forcedNextGoal: AIGoal? = null
    var forcedNextGoalWaitForFinish: Boolean = false

    private val eventPool: EventPool = EventPool().withFilter(EventFilter.containsWorld(entity.world))

    val goals: List<AIGoal> get() = innerGoals.toList()

    init {
        eventPool.on<WorldTickEvent> { event ->
            tick()
        }
    }

    fun forget(key: String) {
        memory.remove(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getMemory(key: String): T? = memory[key]?.value as T

    fun addGoal(goal: AIGoal) {
        innerGoals.sortByDescending { it.priority }
        innerGoals.add(goal)
    }

    fun forceStartGoal(goal: AIGoal, waitForCurrentFinish: Boolean = false) {
        forcedNextGoalWaitForFinish = waitForCurrentFinish
        forcedNextGoal = goal
    }

    fun forceEndCurrentGoal() {
        if (currentGoal != null) {
            currentGoal!!.end()
            currentGoal!!.isRunning = false
        }
    }

    fun tick() {
        if (forcedNextGoal != null && !forcedNextGoalWaitForFinish) {
            if (currentGoal != null) {
                currentGoal!!.end()
                currentGoal!!.isRunning = false
            }
            forcedNextGoal!!.start()
            forcedNextGoal!!.isRunning = true
            return
        }
        goals.forEach { goal ->
            if (!goal.isRunning && goal.cooldown > 0) {
                goal.cooldown--
                return@forEach
            }
            if (!goal.isRunning && goal.startCondition() && goal.cooldown <= 0 && currentGoal == null) {
                val higherPriorityRunning = goals.any { it.isRunning && it.priority > goal.priority }
                if (!higherPriorityRunning) {
                    goal.start()
                    goal.isRunning = true
                    currentGoal = goal
                    return@forEach
                }
            } else if (goal.isRunning && goal.endCondition()) {
                goal.end()
                goal.isRunning = false
                currentGoal = null
            } else if (goal.isRunning) {
                val scheduledRemove = mutableListOf<String>()
                memory.forEach memoryLoop@{
                    if (it.value is ShortTermMemory<*>) {
                        val shortTermMemory = it.value as ShortTermMemory<*>
                        if (shortTermMemory.forgetAfter <= 0) {
                            scheduledRemove.add(it.key)
                            return@memoryLoop
                        }
                        shortTermMemory.forgetAfter--
                    }
                }
                scheduledRemove.forEach(memory::remove)
                goal.tick()
            }
        }
    }

    override fun dispose() {
        eventPool.dispose()
        memory.clear()
        innerGoals.clear()
        currentGoal = null
        forcedNextGoal = null
    }
}

interface AIMemory<T> {
    val value: T
}

data class ShortTermMemory<T>(var forgetAfter: Int, override val value: T) : AIMemory<T>
data class LongTermMemory<T>(override val value: T) : AIMemory<T>

abstract class AIGoal {
    abstract var entity: Entity
    abstract var priority: Int

    var isRunning: Boolean = false
    open var cooldown: Int = 0

    abstract fun startCondition(): Boolean
    abstract fun start()
    abstract fun end()
    abstract fun endCondition(): Boolean
    abstract fun tick()
}

