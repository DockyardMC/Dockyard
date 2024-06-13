package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.sounds.Sound

class ClientboundPlaySoundPacket(sound: Sound): ClientboundPacket(0x66, ProtocolState.PLAY) {

    init {
        data.writeVarInt(0)
        data.writeUtf(sound.identifier)
        data.writeBoolean(false)
        data.writeVarIntEnum<SoundCategory>(sound.category)
        data.writeInt((sound.location.x * 8.0).toInt())
        data.writeInt((sound.location.y * 8.0).toInt())
        data.writeInt((sound.location.z * 8.0).toInt())
        data.writeFloat(sound.volume)
        data.writeFloat(sound.pitch)
        data.writeLong(0L)
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
    VOICE;
}