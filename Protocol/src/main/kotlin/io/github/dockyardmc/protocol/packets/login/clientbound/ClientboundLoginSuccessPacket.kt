package io.github.dockyardmc.protocol.packets.login.clientbound

import io.github.dockyardmc.protocol.types.GameProfile
import io.github.dockyardmc.protocol.Packet
import io.netty.buffer.ByteBuf

class ClientboundLoginSuccessPacket(val gameProfile: GameProfile): Packet {

    override fun write(buffer: ByteBuf) {
        gameProfile.write(buffer)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundLoginSuccessPacket {
            return ClientboundLoginSuccessPacket(GameProfile.read(buffer))
        }
    }
}