package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.commands.CommandNode
import io.github.dockyardmc.commands.writeCommands
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Commands")
@ClientboundPacketInfo(0x11, ProtocolState.PLAY)
class ClientboundCommandsPacket(val commands: MutableMap<Int, CommandNode>): ClientboundPacket() {

    init {
        data.writeCommands(commands)
    }

    fun clone(): ClientboundCommandsPacket = ClientboundCommandsPacket(commands.toMutableMap())
}