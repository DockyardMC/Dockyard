package io.github.dockyardmc.sounds

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityPlaySoundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlaySoundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.SoundCategory
import io.github.dockyardmc.world.World
import kotlin.random.Random

class Sound(var identifier: String, var volume: Float = 0.5f, var pitch: Float = 1.0f, var category: SoundCategory = SoundCategory.MASTER, var seed: Long = Random.nextLong()) {

    init {
        if (!identifier.contains(":")) identifier = "minecraft:$identifier"
    }

}

fun Player.playSound(sound: Sound, location: Location = this.location) {
    val packet = ClientboundPlaySoundPacket(sound, location)
    this.sendPacket(packet)
}

fun Player.playSound(identifier: String, location: Location = this.location, volume: Float = 0.5f, pitch: Float = 1.0f, category: SoundCategory = SoundCategory.MASTER) {
    val sound = Sound(identifier, volume, pitch, category)
    this.playSound(sound, location)
}

fun Player.playSound(identifier: String, volume: Float = 0.5f, pitch: Float = 1.0f, category: SoundCategory = SoundCategory.MASTER) {
    val sound = Sound(identifier, volume, pitch, category)
    this.playSound(sound, this.location)
}

fun Collection<Player>.playSound(sound: Sound, location: Location? = null) {
    this.forEach { it.playSound(sound, location ?: it.location) }
}

fun Collection<Player>.playSound(identifier: String, location: Location? = null, volume: Float = 0.5f, pitch: Float = 1.0f, category: SoundCategory = SoundCategory.MASTER) {
    val sound = Sound(identifier, volume, pitch, category)
    this.playSound(sound, location)
}

fun World.playSound(sound: Sound, location: Location? = null) {
    this.players.playSound(sound, location)
}

fun World.playSound(identifier: String, location: Location? = null, volume: Float = 0.5f, pitch: Float = 1.0f, category: SoundCategory = SoundCategory.MASTER) {
    val sound = Sound(identifier, volume, pitch, category)
    this.playSound(sound, location)
}

fun DockyardServer.playSound(sound: Sound, location: Location? = null) {
    PlayerManager.players.playSound(sound, location)
}

fun DockyardServer.playSound(identifier: String, location: Location? = null, volume: Float = 0.5f, pitch: Float = 1.0f, category: SoundCategory = SoundCategory.MASTER) {
    val sound = Sound(identifier, volume, pitch, category)
    this.playSound(sound, location)
}

fun Player.playSound(sound: Sound, source: Entity) {
    val packet = ClientboundEntityPlaySoundPacket(sound, source)
    this.sendPacket(packet)
}

fun Player.playSound(identifier: String, source: Entity, volume: Float = 0.5f, pitch: Float = 1.0f, category: SoundCategory = SoundCategory.MASTER) {
    val sound = Sound(identifier, volume, pitch, category)
    this.playSound(sound, source)
}

fun Collection<Player>.playSound(sound: Sound, source: Entity) {
    this.forEach { it.playSound(sound, source) }
}

fun Collection<Player>.playSound(identifier: String, source: Entity, volume: Float = 0.5f, pitch: Float = 1.0f, category: SoundCategory = SoundCategory.MASTER) {
    val sound = Sound(identifier, volume, pitch, category)
    this.playSound(sound, source)
}

fun World.playSound(sound: Sound, source: Entity) {
    this.players.playSound(sound, source)
}

fun World.playSound(identifier: String, source: Entity, volume: Float = 0.5f, pitch: Float = 1.0f, category: SoundCategory = SoundCategory.MASTER) {
    val sound = Sound(identifier, volume, pitch, category)
    this.playSound(sound, source)
}