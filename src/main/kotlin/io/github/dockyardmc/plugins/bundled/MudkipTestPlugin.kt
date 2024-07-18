package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.team.Team
import io.github.dockyardmc.team.TeamCollisionRule
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.team.TeamNameTagVisibility

class MudkipTestPlugin: DockyardPlugin {
    override val name = "MudkipTestPlugin"
    override val author = "mudkip"
    override val version = "1.0.0"

    private val team = Team(
        name = "admins",
        displayName = "Admins",
        flags = 0x00,
        teamNameTagVisibility = TeamNameTagVisibility.VISIBLE,
        teamCollisionRule = TeamCollisionRule.ALWAYS,
        color = LegacyTextColor.PINK,
        prefix = "<#9e54ff>[Dev] ",
        suffix = ""
    )

    override fun load(server: DockyardServer) {
        TeamManager.teams.add(team)

        Events.on<PlayerJoinEvent> {
            it.player.team = team
        }
    }

    override fun unload(server: DockyardServer) {
        TeamManager.teams.remove(team)
    }
}