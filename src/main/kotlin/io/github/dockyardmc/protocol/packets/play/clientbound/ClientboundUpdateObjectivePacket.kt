package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scoreboard.Scoreboard
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

private fun writeObjectiveInfo(buffer: ByteBuf, scoreboard: Scoreboard) {
    if (scoreboard.displayName.value != null) {
        buffer.writeNBT(scoreboard.displayName.value!!.toNBT())
    } else {
        buffer.writeNBT(scoreboard.name.toComponent().toNBT())
    }

    buffer.writeVarInt(scoreboard.renderType.value.ordinal)
    buffer.writeBoolean(scoreboard.numberFormat.value != null)
    scoreboard.numberFormat.value?.write(buffer)
}

sealed class Action(val scoreboard: Scoreboard) {
    abstract val id: Int

    abstract fun write(buffer: ByteBuf)
}

class CreateObjective(scoreboard: Scoreboard): Action(scoreboard) {
    override val id = 0

    override fun write(buffer: ByteBuf) {
        writeObjectiveInfo(buffer, scoreboard)
    }
}

class RemoveObjective(scoreboard: Scoreboard): Action(scoreboard) {
    override val id = 1

    override fun write(buffer: ByteBuf) {
        // nothing to write
    }
}

class UpdateObjective(scoreboard: Scoreboard): Action(scoreboard) {
    override val id = 2

    override fun write(buffer: ByteBuf) {
        writeObjectiveInfo(buffer, scoreboard)
    }
}

@WikiVGEntry("Update Objective")
@ClientboundPacketInfo(0x5E, ProtocolState.PLAY)
class ClientboundUpdateObjectivePacket(val action: Action): ClientboundPacket() {
    init {
        data.writeUtf(action.scoreboard.name)
        data.writeByte(action.id)
        action.write(data)
    }
}