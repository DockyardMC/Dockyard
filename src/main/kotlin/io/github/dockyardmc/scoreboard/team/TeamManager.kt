package io.github.dockyardmc.scoreboard.team

import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.CreateTeam
import io.github.dockyardmc.protocol.packets.play.clientbound.RemoveTeam

object TeamManager {
    val teams = BindableMutableList<Team>()

    init {
        teams.itemAdded { event ->
            if (teams.values.any { it.name == event.item.name }) {
                throw IllegalArgumentException("This team already exists!")
            }

            val packet = ClientboundTeamsPacket(CreateTeam(event.item))
            PlayerManager.players.sendPacket(packet)
        }

        teams.itemRemoved { event ->
            val packet = ClientboundTeamsPacket(RemoveTeam(event.item))
            PlayerManager.players.sendPacket(packet)
        }
    }
}