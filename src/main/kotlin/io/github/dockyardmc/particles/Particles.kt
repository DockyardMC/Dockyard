package io.github.dockyardmc.particles

import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSendParticlePacket
import io.github.dockyardmc.registry.Particle
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.World

fun World.spawnParticle(location: Location, particle: Particle, offset: Vector3f = Vector3f(0f, 0f, 0f), speed: Float = 0.5f, count: Int = 1, longDistance: Boolean = false, particleData: ParticleData? = null) {
    this.players.values.spawnParticle(location, particle, offset, speed, count, longDistance, particleData)
}

fun Player.spawnParticle(location: Location, particle: Particle, offset: Vector3f = Vector3f(0f, 0f, 0f), speed: Float = 0.5f, count: Int = 1, longDistance: Boolean = false, particleData: ParticleData? = null) {
    val packet = ClientboundSendParticlePacket(location, particle, offset, speed, count, longDistance, particleData)
    this.sendPacket(packet)
}

fun List<Player>.spawnParticle(location: Location, particle: Particle, offset: Vector3f = Vector3f(0f, 0f, 0f), speed: Float = 0.5f, count: Int = 1, longDistance: Boolean = false, particleData: ParticleData? = null) {
    this.forEach { it.spawnParticle(location, particle, offset, speed, count, longDistance, particleData) }
}