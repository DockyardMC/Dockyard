package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.commands.nodes.CommandNode
import io.github.dockyardmc.commands.nodes.writeCommandNode
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundCommandsPacket(nodes: MutableList<CommandNode>): ClientboundPacket(0x11) {

    init {
        data.writeVarInt(nodes.size)
        nodes.forEach(data::writeCommandNode)
        data.writeVarInt(0)
    }
}