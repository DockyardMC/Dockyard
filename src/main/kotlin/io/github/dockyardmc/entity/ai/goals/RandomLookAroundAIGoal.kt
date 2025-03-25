package io.github.dockyardmc.entity.ai.goals

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.AIGoal
import io.github.dockyardmc.maths.randomInt
import io.github.dockyardmc.maths.vectors.Vector3d
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class RandomLookAroundAIGoal(override var entity: Entity, override var priority: Int, val chancePerTick: Int): AIGoal() {

    var lookTime = 0
    var lookingDirection: Vector3d = Vector3d(0.0)

    override fun startCondition(): Boolean {
        return randomInt(chancePerTick, 100) == chancePerTick
    }

    override fun start() {
        lookTime = 10
        lookingDirection = getRandomDirection()
    }

    override fun tick() {
        lookTime--
        entity.teleport(entity.location.setDirection(lookingDirection))
    }

    override fun end() {
    }

    override fun endCondition(): Boolean = this.lookTime <= 0

    private fun getRandomDirection(): Vector3d {
        val n: Double = Math.PI * 2 * Random().nextDouble()
        return Vector3d(
            cos(n),
            0.0,
            sin(n)
        )
    }
}