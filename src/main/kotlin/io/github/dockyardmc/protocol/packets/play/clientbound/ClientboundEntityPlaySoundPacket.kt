package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.sounds.Sound

class ClientboundEntityPlaySoundPacket(sound: Sound, source: Entity): ClientboundPacket() {

    init {
        data.writeVarInt(0)
        data.writeString(sound.identifier)
        data.writeBoolean(false)
        data.writeVarIntEnum<SoundCategory>(sound.category)
        data.writeVarInt(source.entityId)
        data.writeFloat(sound.volume)
        data.writeFloat(sound.pitch)
        data.writeLong(sound.seed)
    }
}