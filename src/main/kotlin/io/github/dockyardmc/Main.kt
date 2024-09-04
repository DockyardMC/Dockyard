package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.*
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.utils.DebugScoreboard

// This is just maya testing env.. do not actually run this
fun main(args: Array<String>) {

    if(args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }

    if(args.contains("event-documentation")) {
        EventsDocumentationGenerator()
        return
    }

    Events.on<PlayerJoinEvent> {
        val player = it.player
        player.gameMode.value = GameMode.CREATIVE
        DebugScoreboard.sidebar.viewers.add(player)
        player.addPotionEffect(PotionEffects.NIGHT_VISION, 99999, 0, false)
        player.addPotionEffect(PotionEffects.SPEED, 99999, 3, false)
//        player.permissions.add("dockyard.all")
    }

    val test = Commands.subcommandBase("/test")
    test.addSubcommand("no_perms") {
        it.execute { ctx ->
            ctx.sendMessage("aya")
        }
    }

    test.addSubcommand("perms") {
        it.permission = "admin.uwu"
        it.execute { ctx ->
            ctx.sendMessage("owo")
        }
    }

    val server = DockyardServer()
    server.start()
}