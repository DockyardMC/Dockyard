package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.CustomClickActionEvent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.extentions.readNBTCompound
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.readLengthPrefixed
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.kyori.adventure.nbt.CompoundBinaryTag

open class ServerboundCustomClickActionPacket(val id: String, val payload: CompoundBinaryTag?) : ServerboundPacket {
    companion object : NetworkReadable<ServerboundCustomClickActionPacket> {
        override fun read(buffer: ByteBuf): ServerboundCustomClickActionPacket {
            val packet =  ServerboundCustomClickActionPacket(
                buffer.readString(),
                buffer.readOptional { it.readNBTCompound() }
            )
            return packet
        }
    }

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        Events.dispatch(CustomClickActionEvent(processor.player, this.id, this.payload, getPlayerEventContext(processor.player)))
    }
}