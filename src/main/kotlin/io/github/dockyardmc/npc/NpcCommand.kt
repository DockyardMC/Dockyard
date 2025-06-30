package io.github.dockyardmc.npc

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.properStrictCase
import io.github.dockyardmc.extentions.toScrollText
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
                    npc.hologram.addStaticLine("testing testing")
                    npc.hologram.addStaticLine("line 2")
                    npc.hologram.addPlayerLine { p -> "Hi, $p!" }
                    player.sendMessage("<lime>Created npc id <yellow>$id <lime>with name <aqua>$name")
                    npc.skin.value = player.gameProfile.properties.first()

                    npc.onClick.subscribe { (player, clickType) ->
                        broadcastMessage("$player - $clickType")
                    }
                }
            }

            addSubcommand("collision") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("collision", BooleanArgument())
                execute { ctx ->
                    val id = getArgument<String>("id")
                    val npc = npcs[id] ?: throw IllegalArgumentException("Npc with id $id does not exist!")
                    val collision = getArgument<Boolean>("collision")
                    npc.hasCollision.value = collision
                    ctx.sendMessage("<lime>Set collision of npc <yellow>$id<lime> to ${collision.toScrollText()}")
                }
            }

            addSubcommand("look_close") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("look_close", EnumArgument(LookCloseType::class))
                execute { ctx ->
                    val id = getArgument<String>("id")
                    val npc = npcs[id] ?: throw IllegalArgumentException("Npc with id $id does not exist!")
                    val lookCloseType = getEnumArgument<LookCloseType>("look_close")
                    npc.lookClose = lookCloseType
                    ctx.sendMessage("<lime>Set look close type of npc <yellow>$id<lime> to <aqua>${lookCloseType.name.properStrictCase()}")
                }
            }
        }
    }
}