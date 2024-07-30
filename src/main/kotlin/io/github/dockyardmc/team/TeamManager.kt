package io.github.dockyardmc.team

import cz.lukynka.BindableList
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.CreateTeamPacketAction
import io.github.dockyardmc.protocol.packets.play.clientbound.RemoveTeamPacketAction

object TeamManager {
    val teams = BindableList<Team>()

    init {
        teams.itemAdded { event ->
            require(teams[event.item.name] != null) { "This team already exists!" }

            val packet = ClientboundTeamsPacket(CreateTeamPacketAction(event.item))
            PlayerManager.players.sendPacket(packet)
        }

        teams.itemRemoved { event ->
            val packet = ClientboundTeamsPacket(RemoveTeamPacketAction(event.item))
            PlayerManager.players.sendPacket(packet)
        }

    }

    operator fun BindableList<Team>.get(name: String): Team? = this.values.firstOrNull { it.name == name }
}