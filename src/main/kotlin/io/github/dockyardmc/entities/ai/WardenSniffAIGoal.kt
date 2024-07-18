package io.github.dockyardmc.entities.ai

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.plugins.bundled.emberseeker.entities.EmberSeekerWarden
import io.github.dockyardmc.plugins.bundled.emberseeker.entities.WardenAnimation
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.MathUtils

class WardenSniffAIGoal(override var entity: Entity, override var priority: Int) : AIGoal() {

    var sniffTime = 0
    val chancePerTick = 10

    override fun startCondition(): Boolean = MathUtils.randomInt(chancePerTick, 100) == chancePerTick

    override fun start() {
        val warden = entity as EmberSeekerWarden
        warden.playAnimation(WardenAnimation.SNIFF)
        warden.world.playSound("entity.warden.sniff", warden.location, 1f)
        sniffTime = 84
    }

    override fun end() {
        cooldown = MathUtils.randomInt(7, 15) * 20
    }

    override fun endCondition(): Boolean = sniffTime <= 0

    override fun tick() {
        sniffTime--
    }
}