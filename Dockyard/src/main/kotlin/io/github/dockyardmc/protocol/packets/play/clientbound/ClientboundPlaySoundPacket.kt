package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.sounds.Sound

@WikiVGEntry("Sound Effect")
@ClientboundPacketInfo(0x68, ProtocolState.PLAY)
class ClientboundPlaySoundPacket(sound: Sound, location: Location): ClientboundPacket() {

    init {
        data.writeVarInt(0)
        data.writeString(sound.identifier)
        data.writeBoolean(false)
        data.writeVarIntEnum<SoundCategory>(sound.category)
        data.writeInt((location.x * 8.0).toInt())
        data.writeInt((location.y * 8.0).toInt())
        data.writeInt((location.z * 8.0).toInt())
        data.writeFloat(sound.volume)
        data.writeFloat(sound.pitch)
        data.writeLong(sound.seed)
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