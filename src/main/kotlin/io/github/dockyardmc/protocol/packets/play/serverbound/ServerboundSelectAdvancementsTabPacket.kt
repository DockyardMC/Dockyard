package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerSelectAdvancementsTabEvent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSelectAdvancementsTabPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundSelectAdvancementsTabPacket(val action: SelectAdvancementsTabAction, val identifier: String?) : ServerboundPacket {
    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player

        val event = PlayerSelectAdvancementsTabEvent(player, action, identifier)
        Events.dispatch(event)

        if(event.cancelled) {
            player.sendPacket(ClientboundSelectAdvancementsTabPacket(player.advTracker.selectedTab))
            return
        }

        player.advTracker.selectedTab = event.tabId
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundSelectAdvancementsTabPacket {
            val action = buffer.readVarIntEnum<SelectAdvancementsTabAction>()

            val id: String? = if (action == SelectAdvancementsTabAction.OPENED_TAB) {
                buffer.readString()
            } else null

            return ServerboundSelectAdvancementsTabPacket(action, id)

        }
    }

}
enum class SelectAdvancementsTabAction {
    OPENED_TAB,
    CLOSED_SCREEN
}
