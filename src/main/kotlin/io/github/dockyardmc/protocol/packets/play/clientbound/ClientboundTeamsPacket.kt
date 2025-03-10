package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeStringArray
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.team.Team
import io.github.dockyardmc.team.writeTeamInfo
import io.netty.buffer.ByteBuf

class ClientboundTeamsPacket(teamPacketAction: TeamPacketAction) : ClientboundPacket() {
    init {
        data.writeString(teamPacketAction.team.name)
        data.writeByte(teamPacketAction.id.toInt())
        teamPacketAction.write(data)
    }
}

sealed interface TeamPacketAction {
    val id: Byte
    val team: Team
    fun write(buffer: ByteBuf)
}

class CreateTeamPacketAction(override val team: Team) : TeamPacketAction {
    override val id: Byte = 0x00
    override fun write(buffer: ByteBuf) {
        buffer.writeTeamInfo(team)
        buffer.writeStringArray(team.mapEntities())
    }
}

class RemoveTeamPacketAction(override val team: Team) : TeamPacketAction {
    override val id: Byte = 0x01
    override fun write(buffer: ByteBuf) {
        // nothing to write in this packet
    }
}

class UpdateTeamPacketAction(override val team: Team) : TeamPacketAction {
    override val id: Byte = 0x02
    override fun write(buffer: ByteBuf) {
        buffer.writeTeamInfo(team)
    }
}

class AddEntitiesTeamPacketAction(override val team: Team, val entities: Collection<Entity>) : TeamPacketAction {
    override val id: Byte = 0x03

    init {
        require(!entities.any { it !in team.entities.values }) { "This entity is not in the team!" }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeStringArray(team.mapEntities())
    }
}

class RemoveEntitiesTeamPacketAction(override val team: Team, val entities: Collection<Entity>) : TeamPacketAction {
    override val id: Byte = 0x04

    init {
        require(!entities.any { it in team.entities.values }) { "These entities are still in the team!" }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeStringArray(team.mapEntities())
    }
}