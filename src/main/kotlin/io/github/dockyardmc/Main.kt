package io.github.dockyardmc

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.npc.FakePlayer
import io.github.dockyardmc.npc.LookCloseType
import io.github.dockyardmc.player.*
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTeamsPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.CreateTeamPacketAction
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.sidebar.Sidebar
import io.github.dockyardmc.team.TeamCollisionRule
import io.github.dockyardmc.team.TeamManager
import io.github.dockyardmc.team.TeamNameTagVisibility
import io.github.dockyardmc.ui.examples.ExampleCookieClickerScreen
import io.github.dockyardmc.ui.examples.ExampleMinesweeperScreen
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.world.Chunk
import io.github.dockyardmc.world.WorldManager

// This is just testing/development environment.
// To properly use dockyard, visit https://dockyardmc.github.io/Wiki/wiki/quick-start.html

fun main(args: Array<String>) {

    if (args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }

    if (args.contains("event-documentation")) {
        EventsDocumentationGenerator()
        return
    }

    val server = DockyardServer {
        withIp("0.0.0.0")
        withMaxPlayers(50)
        withPort(25565)
        useMojangAuth(true)
        useDebugMode(true)
        withImplementations {
            dockyardCommands = true
        }
    }

    val test = TeamManager.create("test", LegacyTextColor.PINK)


    Events.on<PlayerJoinEvent> {
        val player = it.player

        DockyardServer.broadcastMessage("<yellow>${player} joined the game.")
        player.team.value = test
        player.gameMode.value = GameMode.CREATIVE
        player.permissions.add("dockyard.all")

        DebugScoreboard.sidebar.viewers.add(player)

        player.addPotionEffect(PotionEffects.NIGHT_VISION, 99999, 0, false)
        player.addPotionEffect(PotionEffects.SPEED, 99999, 3, false)
    }

    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<yellow>${it.player} left the game.")
    }


    Commands.add("/reset") {
        execute {
            val platformSize = 30

            val world = WorldManager.mainWorld
            val chunks = mutableListOf<Chunk>()

            for (x in 0 until platformSize) {
                for (z in 0 until platformSize) {
                    world.setBlock(x, 0, z, Blocks.STONE)
                    val chunk = world.getChunkAt(x, z)!!
                    if (!chunks.contains(chunk)) chunks.add(chunk)
                    for (y in 1 until 20) {
                        world.setBlockRaw(x, y, z, Blocks.AIR.defaultBlockStateId, false)
                    }
                }
            }
        }
    }
    server.start()

    val npcs = mutableMapOf<String, FakePlayer>()

    fun suggestNpcIds(player: Player): (Collection<String>) {
        return npcs.keys.toList()
    }

    Commands.add("/npc") {
        addSubcommand("spawn") {
            addArgument("id", StringArgument(), simpleSuggestion("<id>"))
            addArgument("name", StringArgument())
            execute {
                val player = it.getPlayerOrThrow()
                val id = getArgument<String>("id")
                val name = getArgument<String>("name")

                if(npcs[id] != null) throw CommandException("Npc with id $id already exists!")
                val npc = player.world.spawnEntity(FakePlayer(player.location, name)) as FakePlayer
                npcs[id] = npc
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
            }
        }

        addSubcommand("swing_hand") {
            addArgument("id", StringArgument(), ::suggestNpcIds)
            execute {
                val player = it.getPlayerOrThrow()
                val id = getArgument<String>("id")

                val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                npc.swingHand()
            }
        }

        addSubcommand("look_close") {
            addArgument("id", StringArgument(), ::suggestNpcIds)
            addArgument("lookclose", EnumArgument(LookCloseType::class))
            execute {
                val id = getArgument<String>("id")
                val lookClose = getEnumArgument<LookCloseType>("lookclose")

                val npc = npcs[id] ?: throw CommandException("Npc with id $id does not exist!")
                npc.lookClose = lookClose
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
            }
        }
    }

    Commands.add("/cookie") {
        execute {
            val player = it.getPlayerOrThrow()
            player.openInventory(ExampleCookieClickerScreen(player))
        }
    }

    Commands.add("/minesweeper") {
        addArgument("mines", IntArgument(), simpleSuggestion("<num of mines>"))
        execute {
            val player = it.getPlayerOrThrow()
            val mines = getArgument<Int>("mines")
            player.openInventory(ExampleMinesweeperScreen(player, mines))
        }
    }
}