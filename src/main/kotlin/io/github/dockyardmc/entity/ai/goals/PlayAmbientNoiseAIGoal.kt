package io.github.dockyardmc.entity.ai.goals

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.AIGoal
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.utils.randomInt

class PlayAmbientNoiseAIGoal(override var entity: Entity, override var priority: Int, val chancePerTick: Int, val sound: String): AIGoal() {

    override fun startCondition(): Boolean {
        return randomInt(chancePerTick, 100) == chancePerTick
    }

    override fun start() {
        entity.playSoundToViewers(Sound(sound), entity.location)
    }

    override fun tick() {
    }

    override fun end() {
    }

    override fun endCondition(): Boolean = true
}