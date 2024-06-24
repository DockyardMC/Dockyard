package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeStringArray
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.scoreboard.team.Team
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

sealed class Action(val team: Team) {
    abstract val id: Int

    abstract fun write(buffer: ByteBuf)
}

class CreateTeam(team: Team): Action(team) {
    override val id: Int = 0x00

    override fun write(buffer: ByteBuf) {
        buffer.writeNBT(team.displayName.toNBT())
        buffer.writeByte(team.flags)
        buffer.writeUtf(team.nameTagVisibility.value)
        buffer.writeUtf(team.collisionRule.value)
        buffer.writeVarInt(team.color)
        buffer.writeNBT(team.prefix.toNBT())
        buffer.writeNBT(team.suffix.toNBT())
        buffer.writeStringArray(team.mapEntities())
    }
}

class RemoveTeam(team: Team): Action(team) {
    override val id = 0x01

    override fun write(buffer: ByteBuf) {
        // nothing to write
    }
}

class UpdateTeam(team: Team): Action(team) {
    override val id = 0x02

    override fun write(buffer: ByteBuf) {
        buffer.writeNBT(team.displayName.toNBT())
        buffer.writeByte(team.flags)
        buffer.writeUtf(team.nameTagVisibility.value)
        buffer.writeUtf(team.collisionRule.value)
        buffer.writeVarInt(team.color)
        buffer.writeNBT(team.prefix.toNBT())
        buffer.writeNBT(team.suffix.toNBT())
    }
}

class AddEntities(team: Team, val entities: Collection<Entity>): Action(team) {
    override val id = 0x03

    init {
        if (entities.any { it in team.entities.values }) {
            throw IllegalArgumentException("This entity is already in the team!")
        }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeStringArray(team.mapEntities())
    }
}

class RemoveEntities(team: Team, val entities: Collection<Entity>): Action(team) {
    override val id = 0x04

    init {
        if (team.entities.values.containsAll(entities)) {
            throw IllegalArgumentException("These entities aren't in the team!")
        }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeStringArray(team.mapEntities())
    }
}

@WikiVGEntry("Update Teams")
@ClientboundPacketInfo(0x60, ProtocolState.PLAY)
class ClientboundTeamsPacket(
    action: Action
): ClientboundPacket() {
    init {
        data.writeUtf(action.team.name)
        data.writeByte(action.id)
        action.write(data)
    }
}