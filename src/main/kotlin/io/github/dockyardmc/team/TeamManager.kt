package io.github.dockyardmc.team

import cz.lukynka.bindables.BindableList
import cz.lukynka.bindables.BindableMap
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.CreateTeamPacketAction
import io.github.dockyardmc.protocol.packets.play.clientbound.RemoveTeamPacketAction
import io.github.dockyardmc.scroll.LegacyTextColor

object TeamManager {
    val teams = BindableMap<String, Team>()

    fun create(name: String, color: LegacyTextColor, teamNameTagVisibility: TeamNameTagVisibility = TeamNameTagVisibility.VISIBLE, teamCollisionRule: TeamCollisionRule = TeamCollisionRule.ALWAYS, displayName: String = name, prefix: String? = null, suffix: String? = null): Team {
        require(teams[name] == null) { "Team with name $name already exists!" }

        val team = Team(name, color, teamNameTagVisibility, teamCollisionRule, displayName, prefix, suffix)
        val teamCreatePacket = ClientboundTeamsPacket(CreateTeamPacketAction(team))
        PlayerManager.players.sendPacket(teamCreatePacket)
        teams[name] = team

        return team
    }

    fun remove(name: String) {
        val team = teams[name]
        requireNotNull(team) { "Team with name $name does not exist!" }
        team.entities.values.forEach(team.entities::remove)
        val removePacket = ClientboundTeamsPacket(RemoveTeamPacketAction(team))
        PlayerManager.players.sendPacket(removePacket)
        teams.remove(name)
    }

    fun remove(team: Team) {
        remove(team.name)
    }

    operator fun BindableList<Team>.get(name: String): Team? = this.values.firstOrNull { it.name == name }
}