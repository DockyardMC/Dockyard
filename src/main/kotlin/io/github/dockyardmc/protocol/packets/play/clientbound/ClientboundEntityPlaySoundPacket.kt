package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.sounds.Sound

class ClientboundEntityPlaySoundPacket(sound: Sound, source: Entity): ClientboundPacket() {

    init {
        buffer.writeVarInt(0)
        buffer.writeString(sound.identifier)
        buffer.writeBoolean(false)
        buffer.writeEnum<SoundCategory>(sound.category)
        buffer.writeVarInt(source.id)
        buffer.writeFloat(sound.volume)
        buffer.writeFloat(sound.pitch)
        buffer.writeLong(sound.seed)
    }
}