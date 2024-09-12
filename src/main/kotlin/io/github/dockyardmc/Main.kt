package io.github.dockyardmc

import io.github.dockyardmc.commands.CommandSuggestions
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.commands.SuggestionProvider
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.entities.BlockDisplay
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.player.*
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.resourcepack.addResourcepack
import io.github.dockyardmc.resourcepack.removeResourcepack
import io.github.dockyardmc.runnables.runLaterAsync
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.utils.debug
import java.util.Vector

// This is just testing/development environment.
// To properly use dockyard, visit https://dockyardmc.github.io/Wiki/wiki/quick-start.html

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

        DockyardServer.broadcastMessage("<yellow>${player} joined the game.")
        player.gameMode.value = GameMode.CREATIVE
        player.permissions.add("dockyard.all")

        DebugScoreboard.sidebar.viewers.add(player)

        player.addPotionEffect(PotionEffects.NIGHT_VISION, 99999, 0, false)
        player.addPotionEffect(PotionEffects.SPEED, 99999, 3, false)
    }

    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<yellow>${it.player} left the game.")
    }

    fun getPackSuggestions(): CommandSuggestions {
        return SuggestionProvider.withContext { it.resourcepacks.keys.toList() }
    }

    Commands.add("/test") {
        execute {
            val player = it.getPlayerOrThrow()
            val test = player.world.spawnEntity(BlockDisplay(player.location)) as BlockDisplay
            test.autoViewable = false
            test.block.value = Blocks.REDSTONE_BLOCK
            test.translateTo(Vector3f(-0.5f), 0)
            test.addViewer(player)
            runLaterAsync(1) {
                test.translateTo(Vector3f(0f, 2f, 0f), 20)
                test.scaleTo(0f, 0f, 0f, 20)
            }
        }
    }

    Commands.add("/resourcepack") {
        addSubcommand("add") {
            addArgument("name", StringArgument(), getPackSuggestions())
            addArgument("url", StringArgument())
            execute {
                val player = it.getPlayerOrThrow()
                val name = getArgument<String>("name")
                val url = getArgument<String>("url")

                DockyardServer.broadcastMessage(url)

                player.addResourcepack(name) {
                    withUrl(url)

                    onSuccess { response ->
                        response.player.sendMessage("<lime>Successfully loaded resourcepack!")
                    }
                    onFail { response ->
                        response.player.sendMessage("<red>womp womp: <orange>${response.status.name}")
                    }
                }
            }
        }

        addSubcommand("remove") {
            addArgument("name", StringArgument(), getPackSuggestions())
            execute {
                val player = it.getPlayerOrThrow()
                val name = getArgument<String>("name")

                player.removeResourcepack(name)
            }
        }
    }

    val server = DockyardServer()
    server.start()
}