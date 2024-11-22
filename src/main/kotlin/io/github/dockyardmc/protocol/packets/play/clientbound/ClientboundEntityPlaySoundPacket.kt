package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.sounds.Sound

@WikiVGEntry("Entity Sound Effect")
@ClientboundPacketInfo(0x67, ProtocolState.PLAY)
class ClientboundEntityPlaySoundPacket(sound: Sound, source: Entity): ClientboundPacket() {

    init {
        data.writeVarInt(0)
        data.writeUtf(sound.identifier)
        data.writeBoolean(false)
        data.writeVarIntEnum<SoundCategory>(sound.category)
        data.writeVarInt(source.entityId)
        data.writeFloat(sound.volume)
        data.writeFloat(sound.pitch)
        data.writeLong(sound.seed)
    }
}