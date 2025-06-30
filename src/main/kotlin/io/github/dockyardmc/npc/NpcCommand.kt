package io.github.dockyardmc.npc

import io.github.dockyardmc.commands.CommandException
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.commands.simpleSuggestion
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.player.Player
import kotlin.collections.set

class NpcCommand {

    val npcs = mutableMapOf<String, FakePlayer>()

    fun suggestNpcIds(player: Player): (Collection<String>) {
        return npcs.keys.toList()
    }

    init {
        Commands.add("/npc") {
            withDescription("Utility command to manually manage spawned npcs")
            withPermission("dockyard.commands.npc")

            addSubcommand("create") {
                addArgument("id", StringArgument(), simpleSuggestion("<id>"))
                addArgument("name", StringArgument())
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val name = getArgument<String>("name")

                    if (npcs[id] != null) throw CommandException("Npc with id $id already exists!")
                    val npc = player.world.spawnEntity<FakePlayer>(FakePlayer(player.location))
                    npcs[id] = npc
                    player.sendMessage("<lime>Created npc id <yellow>$id <lime>with name <aqua>$name")
                }
            }
        }
    }
}