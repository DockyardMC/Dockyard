package io.github.dockyardmc.sounds

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlaySoundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.SoundCategory
import io.github.dockyardmc.world.World
import io.netty.buffer.ByteBuf

class Sound(var identifier: String, var volume: Float = 0.5f, var pitch: Float = 1.0f, var category: SoundCategory = SoundCategory.MASTER) {

    init {
        if(!identifier.contains(":")) identifier = "minecraft:$identifier"
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
    this.playSound(sound)
}

fun World.playSound(sound: Sound, location: Location? = null) {
    this.players.values.playSound(sound, location)
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

fun ByteBuf.writeSoundEvent(sound: String) {
    this.writeVarInt(0)
    this.writeUtf(sound)
    this.writeBoolean(false)
}

fun ByteBuf.readSoundEvent(): String {
    val type = this.readVarInt()
    val identifier = this.readString()
    val hasFixedRange = this.readBoolean()
    if(hasFixedRange) {
        val fixedRange = this.readFloat()
    }
    return identifier
}