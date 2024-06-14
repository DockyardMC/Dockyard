package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.commands.CommandNode
import io.github.dockyardmc.commands.writeCommandNode
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Commands")
class ClientboundCommandsPacket(nodes: MutableList<CommandNode>): ClientboundPacket(0x11, ProtocolState.PLAY) {

    init {
        data.writeVarInt(nodes.size)
        nodes.forEach(data::writeCommandNode)
        data.writeVarInt(0)
    }
}