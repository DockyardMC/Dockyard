package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.commands.CommandNode
import io.github.dockyardmc.commands.writeCommands
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundCommandsPacket(val commands: MutableMap<Int, CommandNode>) : ClientboundPacket() {

    init {
        buffer.writeCommands(commands)
    }

    fun clone(): ClientboundCommandsPacket = ClientboundCommandsPacket(commands.toMutableMap())
}