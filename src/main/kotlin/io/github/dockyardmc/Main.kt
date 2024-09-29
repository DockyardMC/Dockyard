package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.extentions.toRgbInt
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.add
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.utils.customBiome
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.world.Chunk
import io.github.dockyardmc.world.WorldManager

// This is just testing/development environment.
// To properly use dockyard, visit https://dockyardmc.github.io/Wiki/wiki/quick-start.html

fun main(args: Array<String>) {

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

    if (args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }

    if (args.contains("event-documentation")) {
        EventsDocumentationGenerator()
        return
    }

    val customBiome = customBiome("dockyardmc:the_pale_garden") {
        withSkyColor("#c9c9c9")
        withGrassColor("#a9ada8")
        withFogColor("#ffffff")
        withFoliageColor("#d7d1de")
        withParticles(Particles.ASH, 0.05f)
        withWaterColor("#a676de")
    }
    BiomeRegistry.addEntry(customBiome)

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

    Commands.add("/reset") {
        execute {
            val platformSize = 30

            val world = WorldManager.mainWorld
            val chunks = mutableListOf<Chunk>()

            val hollow = BiomeRegistry["dockyardmc:the_pale_garden"]
            debug("${hollow.identifier} - ${hollow.getProtocolId()}", true)

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
            chunks.forEach { chunk ->
                chunk.sections.forEach {
                    it.biomePalette.fill(hollow.getProtocolId())

                }
                chunk.updateCache()
                PlayerManager.players.sendPacket(chunk.packet)
            }
        }
    }


    server.start()
}