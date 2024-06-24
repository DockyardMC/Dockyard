package io.github.dockyardmc.scoreboard

import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundResetScorePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundUpdateObjectivePacket
import io.github.dockyardmc.protocol.packets.play.clientbound.CreateObjective
import io.github.dockyardmc.protocol.packets.play.clientbound.RemoveObjective

object ScoreboardManager {
    val scoreboards = BindableMutableList<Scoreboard>()

    init {
        scoreboards.itemAdded { event ->
            val scoreboard = event.item
            val packet = ClientboundUpdateObjectivePacket(CreateObjective(scoreboard))
            PlayerManager.players.sendPacket(packet)
        }

        scoreboards.itemRemoved { event ->
            val scoreboard = event.item
            val packet = ClientboundUpdateObjectivePacket(RemoveObjective(scoreboard))
            PlayerManager.players.sendPacket(packet)
        }
    }

    fun resetScores(entity: Entity) {
        entity.viewers.forEach {
            it.sendPacket(ClientboundResetScorePacket(entity))
        }
    }
}