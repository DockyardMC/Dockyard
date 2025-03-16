package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeByte
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundStopSoundPacket(flags: Byte, category: SoundCategory?, sound: String?): ClientboundPacket() {

    init {
        buffer.writeByte(flags)
        if((flags == 3.toByte() || flags == 1.toByte()) && category != null) {
            buffer.writeVarInt(category.ordinal)
        }
        if((flags == 2.toByte() || flags == 3.toByte()) && sound != null) {
            buffer.writeString(sound)
        }
    }

}