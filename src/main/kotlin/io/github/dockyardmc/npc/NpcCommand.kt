package io.github.dockyardmc.npc

import io.github.dockyardmc.commands.BooleanArgument
import io.github.dockyardmc.commands.CommandException
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.commands.LegacyTextColorArgument
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.commands.simpleSuggestion
import io.github.dockyardmc.entities.EntityManager.despawnEntity
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.extentions.toScrollText
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.scroll.LegacyTextColor
import kotlin.collections.set

class NpcCommand {

    val npcs = mutableMapOf<String, PlayerNpc>()

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
                    val npc = player.world.spawnEntity(PlayerNpc(player.location, name)) as PlayerNpc
                    npcs[id] = npc
                    player.sendMessage("<lime>Created npc id <yellow>$id <lime>with name <aqua>$name")
                }
            }

            addSubcommand("auto_viewable") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("autoviewable", BooleanArgument())
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val autoViewable = getArgument<Boolean>("autoviewable")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")

                    npc.autoViewable = autoViewable
                    player.sendMessage("<lime>Set auto viewable value of npc <yellow>$id <lime>to ${autoViewable.toScrollText()}")
                }
            }

            addSubcommand("viewer") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("action", EnumArgument(NpcViewerAction::class))
                addArgument("player", PlayerArgument())
                execute {
                    val executor = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val action = getEnumArgument<NpcViewerAction>("action")
                    val player = getArgument<Player>("player")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")

                    if (npc.autoViewable) throw CommandException("Npc id $id is auto-viewable! You cannot change viewer list manually")

                    when (action) {
                        NpcViewerAction.ADD -> {
                            if (npc.viewers.contains(player)) throw CommandException("Player $player is already a viewer of npc $id")
                            npc.addViewer(player)
                            executor.sendMessage("<lime>Added <aqua>$player <lime>to viewer list of npc <yellow>$id")
                        }

                        NpcViewerAction.REMOVE -> {
                            if (!npc.viewers.contains(player)) throw CommandException("Player $player is not a viewer of npc $id")
                            npc.removeViewer(player, false)
                            executor.sendMessage("<lime>Removed <aqua>$player <lime>from viewer list of npc <yellow>$id")
                        }
                    }
                }
            }

            addSubcommand("remove") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                    npc.world.despawnEntity(npc)
                    npcs.remove(id)
                    player.sendMessage("<lime>Removed npc id <yellow>$id<lime>!")
                }
            }

            addSubcommand("skin") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("name", StringArgument())
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val name = getArgument<String>("name")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                    npc.setSkin(name)
                    player.sendMessage("<lime>Set skin of npc <yellow>$id <lime>to <aqua>$name")
                }
            }

            addSubcommand("pose") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("pose", EnumArgument(EntityPose::class))
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val pose = getEnumArgument<EntityPose>("pose")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                    npc.pose.value = pose
                    player.sendMessage("<lime>Set pose of npc <yellow>$id <lime>to <aqua>${pose.name.lowercase()}")
                }
            }

            addSubcommand("color") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("color", LegacyTextColorArgument())
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val color = getArgument<LegacyTextColor>("color")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                    npc.teamColor.value = color
                    player.sendMessage("<lime>Set team color of npc <yellow>$id <lime>to <${color.hex}>${color.name.lowercase()}")
                }
            }

            addSubcommand("listed") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("listed", BooleanArgument())
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val listed = getArgument<Boolean>("listed")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                    npc.isListed.value = listed
                    player.sendMessage("<lime>Set listed value of npc <yellow>$id <lime>to ${listed.toScrollText()}")
                }
            }

            addSubcommand("swing_hand") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                execute {
                    val id = getArgument<String>("id")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                    npc.swingHand()
                }
            }

            addSubcommand("look_close") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("lookclose", EnumArgument(LookCloseType::class))
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val lookClose = getEnumArgument<LookCloseType>("lookclose")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                    npc.lookClose = lookClose
                    player.sendMessage("<lime>Set look close type of npc <yellow>$id <lime>to <aqua>${lookClose.name.lowercase()}")
                }
            }

            addSubcommand("nametag_visibility") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("visible", BooleanArgument())
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val visible = getArgument<Boolean>("visible")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                    npc.nametagVisible.value = visible
                    player.sendMessage("<lime>Set nametag visibility of npc <yellow>$id <lime>to ${visible.toScrollText()}")
                }
            }

            addSubcommand("collision") {
                addArgument("id", StringArgument(), ::suggestNpcIds)
                addArgument("has_collision", BooleanArgument())
                execute {
                    val player = it.getPlayerOrThrow()
                    val id = getArgument<String>("id")
                    val collision = getArgument<Boolean>("has_collision")

                    val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                    npc.hasCollision.value = collision
                    player.sendMessage("<lime>Set collision of npc <yellow>$id <lime>to ${collision.toScrollText()}")
                }
            }
        }
    }

    enum class NpcViewerAction {
        ADD,
        REMOVE,
    }
}
