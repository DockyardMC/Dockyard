package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class OminousBattleAmplifier(val amplifier: Int) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(amplifier)
    }

    companion object : NetworkReadable<OminousBattleAmplifier> {
        override fun read(buffer: ByteBuf): OminousBattleAmplifier {
            return OminousBattleAmplifier(buffer.readVarInt())
        }
    }
}