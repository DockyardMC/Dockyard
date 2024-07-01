package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.IntArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.scroll.TextColor
import io.github.dockyardmc.team.Team
import io.github.dockyardmc.team.TeamCollisionRule
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.team.TeamNameTagVisibility

class MudkipTestPlugin: DockyardPlugin {
    override val name = "MudkipTestPlugin"
    override val author = "mudkip"
    override val version = "1.0.0"

    private val team = Team(
        "admins",
        "Admins",
        0x00,
        TeamNameTagVisibility.VISIBLE,
        TeamCollisionRule.ALWAYS,
        TextColor.LIME,
        "<#9e54ff>[Dev] ",
        ""
    )

    override fun load(server: DockyardServer) {
        TeamManager.teams.add(team)

        Events.on<PlayerJoinEvent> {
            it.player.team = team
        }

        Commands.add("/color") {
            it.addArgument("color", IntArgument())

            it.execute { executor ->
                val color = it.get<Int>("color")
                val team = executor.player?.team

                if (team != null) {
                    team.color.value = color
                }
            }
        }
    }

    override fun unload(server: DockyardServer) {
        TeamManager.teams.remove(team)
    }
}