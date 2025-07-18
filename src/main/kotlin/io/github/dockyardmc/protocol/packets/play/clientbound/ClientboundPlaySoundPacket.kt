package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.sounds.Sound

class ClientboundPlaySoundPacket(sound: Sound, location: Location) : ClientboundPacket() {

    init {
        buffer.writeVarInt(0)
        buffer.writeString(sound.identifier)
        buffer.writeBoolean(false)
        buffer.writeEnum<SoundCategory>(sound.category)
        buffer.writeInt((location.x * 8.0).toInt())
        buffer.writeInt((location.y * 8.0).toInt())
        buffer.writeInt((location.z * 8.0).toInt())
        buffer.writeFloat(sound.volume)
        buffer.writeFloat(sound.pitch)
        buffer.writeLong(sound.seed)
    }
}

enum class SoundCategory {
    MASTER,
    MUSIC,
    RECORDS,
    WEATHER,
    BLOCKS,
    HOSTILE,
    NEUTRAL,
    PLAYERS,
    AMBIENT,
    VOICE,
    UI;
}