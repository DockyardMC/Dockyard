package io.github.dockyardmc

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.entities.EntityManager.despawnEntity
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.entities.TestZombie
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.add
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.registry.registries.Biome
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.world.Chunk
import io.github.dockyardmc.world.WorldManager
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import kotlin.math.log


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

    if (args.contains("registry-test")) {

        val stream = ClassLoader.getSystemResource("registry/biome_registry.json.gz").openStream()
        val gzip = GZIPInputStream(stream)
        val reader: InputStreamReader = InputStreamReader(gzip)
        val `in` = BufferedReader(reader)

        var readed: String?
        var final: String = ""
        while ((`in`.readLine().also { readed = it }) != null) {
            println(readed)
            final = readed.toString()
        }

        val test = Json.decodeFromString<List<Biome>>(final)
        log(test.toString(), LogType.AUDIT)

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

    var entity: Entity? = null

    Commands.add("/entity") {
        addSubcommand("spawn") {
            execute {
                val player = it.getPlayerOrThrow()
                val location = player.location
                entity = location.world.spawnEntity(TestZombie(location))
            }
        }
        addSubcommand("kill") {
            execute {
                entity!!.world.despawnEntity(entity!!)
            }
        }
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
            chunks.forEach { chunk ->
                chunk.updateCache()
                PlayerManager.players.sendPacket(chunk.packet)
            }
        }
    }

    val server = DockyardServer {
        withIp("0.0.0.0")
        withMaxPlayers(50)
        withPort(25565)
        useMojangAuth(false)
        useDebugMode(true)
        withImplementations {
            dockyardCommands = true
        }
    }
    server.start()
}