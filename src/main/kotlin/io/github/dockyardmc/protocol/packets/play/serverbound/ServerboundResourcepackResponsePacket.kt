package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.resourcepack.ResourcepackManager
import io.github.dockyardmc.resourcepack.ResourcepackResponseEvent
import io.github.dockyardmc.resourcepack.ResourcepackStatus
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.util.*

class ServerboundResourcepackResponsePacket(var uuid: UUID, var response: ResourcepackStatus) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {

        val player = processor.player

        ResourcepackManager.pending.toList().forEach {
            if (it.uuid != uuid) return@forEach
            val event = ResourcepackResponseEvent(it, player, response)

            when (response) {
                ResourcepackStatus.SUCCESSFULLY_LOADED -> {
                    it.onSuccess?.invoke(event)
                    player.resourcepacks[it.name] = it
                    ResourcepackManager.pending.remove(it)
                }

                ResourcepackStatus.DECLINED,
                ResourcepackStatus.FAILED_TO_DOWNLOAD,
                ResourcepackStatus.INVALID_URL,
                ResourcepackStatus.FAILED_TO_RELOAD,
                ResourcepackStatus.DISCARDED -> {
                    it.onFail?.invoke(event)
                }

                ResourcepackStatus.DOWNLOADED,
                ResourcepackStatus.ACCEPTED -> {
                }
            }
        }

    }

    companion object {
        fun read(buf: ByteBuf): ServerboundResourcepackResponsePacket {
            return ServerboundResourcepackResponsePacket(buf.readUUID(), buf.readEnum<ResourcepackStatus>())
        }
    }
}