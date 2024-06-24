package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.CreateTeam
import io.github.dockyardmc.scoreboard.team.Team
import io.github.dockyardmc.scoreboard.team.TeamManager
import io.github.dockyardmc.scroll.extensions.toComponent

class MudkipTestPlugin: DockyardPlugin {
    override val name = "MudkipTestPlugin"
    override val author = "mudkip"
    override val version = "1.0.0"

    override fun load(server: DockyardServer) {
        val team = Team(
            "admins",
            color = 12,
            prefix = "<red>[ADMIN] ".toComponent()
        )

        TeamManager.teams.add(team)

        Events.on<PlayerJoinEvent> {
            it.player.team = team
        }
    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}