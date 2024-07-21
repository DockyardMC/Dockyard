package io.github.dockyardmc.entities.ai

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.particles.VibrationParticleData
import io.github.dockyardmc.particles.VibrationSource
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.plugins.bundled.emberseeker.entities.EmberSeekerWarden
import io.github.dockyardmc.entities.WardenAnimation
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.toVector3

class WardenAttackPlayerGoal(override var entity: Entity, override var priority: Int) : AIGoal() {

    val warden = entity as EmberSeekerWarden
    var angered = 0

    override fun startCondition(): Boolean  = warden.ai.memory.containsKey("target")

    override fun start() {
        val memory = warden.ai.getMemory<Player>("target")!!
        warden.playAnimation(WardenAnimation.ROAR)
        warden.world.playSound("entity.warden.roar", warden.location, 1f)
        warden.world.playSound("entity.warden.roar", warden.location, 0.6f)
        warden.world.playSound("entity.warden.tendril_clicks", warden.location, 0.8f)
        warden.angerLevel.value = 600
        angered = MathUtils.randomInt(100, 500)
    }

    override fun end() {
        warden.angerLevel.value = 0
        warden.ai.forget("target")
    }

    override fun endCondition(): Boolean = angered <= 0 || warden.ai.getMemory<Player>("target") == null

    override fun tick() {
        val memory = warden.ai.getMemory<Player>("target")!!
        warden.playAnimation(WardenAnimation.TENDRIL_SHAKE)
        warden.world.spawnParticle(warden.location, Particles.VIBRATION, particleData = VibrationParticleData(VibrationSource.ENTITY, memory.location.toVector3(), memory.entityId, 1.5f, 10))
        angered--
        //TODO Pathfinder
    }
}