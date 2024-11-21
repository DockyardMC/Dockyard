package io.github.dockyardmc.entity.ai

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.extentions.broadcastActionBar
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.utils.randomInt
import io.github.dockyardmc.utils.vectors.Vector3f
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class AIManager(val entity: Entity) {
    val memory: MutableMap<String, AIMemory<*>> = mutableMapOf()
    private val innerGoals: MutableList<AIGoal> = mutableListOf()
    var currentGoal: AIGoal? = null
    var forcedNextGoal: AIGoal? = null
    var forcedNextGoalWaitForFinish: Boolean = false

    val goals: List<AIGoal> get() = innerGoals.toList()

    init {
        Events.on<ServerTickEvent> {
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
        if(currentGoal != null) {
            currentGoal!!.end()
            currentGoal!!.isRunning = false
        }
    }

    fun tick() {
        DockyardServer.broadcastActionBar("<yellow>$currentGoal <dark_gray>| <gray>${currentGoal?.cooldown}")
        if(forcedNextGoal != null && !forcedNextGoalWaitForFinish) {
            if(currentGoal != null) {
                currentGoal!!.end()
                currentGoal!!.isRunning = false
            }
            forcedNextGoal!!.start()
            forcedNextGoal!!.isRunning = true
            return
        }
        goals.forEach { goal ->
            if(!goal.isRunning && goal.cooldown > 0) {
                goal.cooldown--
                return@forEach
            }
            if(!goal.isRunning && goal.startCondition() && goal.cooldown <= 0 && currentGoal == null) {
                val higherPriorityRunning = goals.any { it.isRunning && it.priority > goal.priority}
                if(!higherPriorityRunning) {
                    goal.start()
                    goal.isRunning = true
                    currentGoal = goal
                    return@forEach
                }
            } else if(goal.isRunning && goal.endCondition()) {
                goal.end()
                goal.isRunning = false
                currentGoal = null
            } else if(goal.isRunning) {
                val scheduledRemove = mutableListOf<String>()
                memory.forEach memoryLoop@{
                    if(it.value is ShortTermMemory<*>) {
                        val shortTermMemory = it.value as ShortTermMemory<*>
                        if(shortTermMemory.forgetAfter <= 0) {
                            scheduledRemove.add(it.key)
                            return@memoryLoop
                        }
                        shortTermMemory.forgetAfter--
                    }
                }
                scheduledRemove.forEach(memory::remove)
                if(scheduledRemove.size > 0) DockyardServer.broadcastMessage("<orange>forgot $scheduledRemove")
                goal.tick()
            }
        }
    }
}

interface AIMemory<T> {
    val value: T
}

data class ShortTermMemory<T>(var forgetAfter: Int, override val value: T): AIMemory<T>
data class LongTermMemory<T>(override val value: T): AIMemory<T>

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

class RandomLookAroundAIGoal(override var entity: Entity, override var priority: Int): AIGoal() {

    val chancePerTick = 50
    var lookTime = 0
    var lookingDirection: Vector3f = Vector3f(0f)

    override fun startCondition(): Boolean {
        if(randomInt(chancePerTick, 100) != chancePerTick) {
            return false
        }
        return entity.health.value > 10
    }

    override fun start() {
        lookTime = randomInt(20, 40)
        lookingDirection = getRandomDirection()
        DockyardServer.broadcastMessage("<lime>started ai")
    }

    override fun tick() {
        lookTime--
        entity.location = entity.location.add(lookingDirection)
    }

    override fun end() {
        DockyardServer.broadcastMessage("<red>ended ai")
    }

    override fun endCondition(): Boolean = this.lookTime <= 0

    private fun getRandomDirection(): Vector3f {
        val n: Double = Math.PI * 2 * Random().nextDouble()
        return Vector3f(
            cos(n).toFloat(),
            0f,
            sin(n).toFloat()
        )
    }
}