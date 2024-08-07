package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.bossbar.Bossbar
import io.github.dockyardmc.bossbar.BossbarColor
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.events.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.player.add
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEffectPacket
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.schematics.SchematicReader
import io.github.dockyardmc.schematics.placeSchematic
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.serverlinks.DefaultServerLinkType
import io.github.dockyardmc.serverlinks.DefaultServerLink
import io.github.dockyardmc.serverlinks.CustomServerLink
import io.github.dockyardmc.serverlinks.ServerLinks
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.WorldManager
import java.io.File

class MayaTestPlugin: DockyardPlugin {
    override var name: String = "MayaTestPlugin"
    override val author: String = "LukynkaCZE"
    override val version: String = DockyardServer.versionInfo.dockyardVersion

    override fun load(server: DockyardServer) {

        val serverBar = Bossbar(
            "<aqua>DockyardMC <dark_gray>| <gray>Version ${DockyardServer.versionInfo.dockyardVersion} (${DockyardServer.versionInfo.gitBranch})",
            1f,
            BossbarColor.BLUE
        )

        Events.on<PlayerJoinEvent> {
            val effectPacket = ClientboundEntityEffectPacket(it.player, 15, 1, 99999, 0x00)
            it.player.sendPacket(effectPacket)

            it.player.tabListHeader.value =
                "\n  <dark_gray><s>        <r>  <aqua>DockyardMC<r>  <dark_gray><s>        <r>  \n".toComponent()
            it.player.tabListFooter.value = "\n  <dark_gray><s>                                   <r>  \n".toComponent()
            serverBar.viewers.add(it.player)
        }

        ServerLinks.links.add(CustomServerLink("<aqua>Github", "https://github.com/DockyardMC/Dockyard"))
        ServerLinks.links.add(CustomServerLink("<aqua>Discord", "https://discord.gg/SA9nmfMkdc"))
        ServerLinks.links.add(
            DefaultServerLink(
                DefaultServerLinkType.BUG_REPORT,
                "https://github.com/DockyardMC/Dockyard/issues"
            )
        )

        Commands.add("/world") { cmd ->
            cmd.addArgument("world", StringArgument())
            cmd.execute { executor ->
                val player = executor.player!!
                val world = WorldManager.getOrThrow(cmd.get<String>("world"))
                world.join(player)
            }
        }

        val warps = mutableMapOf<String, Location>()

        Commands.add("/setwarp") {
            it.addArgument("name", StringArgument())
            it.execute { ctx ->
                val player = ctx.player!!
                val name = it.get<String>("name")
                val location = player.location

                warps[name] = location
                player.sendMessage("<yellow>Set warp <lime>$name<yellow> to <pink>$location")
            }
        }

        Commands.add("/warp") {
            it.addArgument("name", StringArgument())
            it.execute { ctx ->
                val player = ctx.player!!
                val name = it.get<String>("name")
                val location = warps[name] ?: throw Exception("This warp not exist!")
                player.teleport(location)
                player.playSound("entity.enderman.teleport", player.location, 1f, 2f)
                player.spawnParticle(player.location.add(Vector3f(0f, 0.5f, 0f)), Particles.PORTAL, Vector3f(0f), 1f,15)
            }
        }

        Commands.add("/paste") {
            it.addArgument("schematic", StringArgument())
            it.execute { ctx ->
                val player = ctx.playerOrThrow()
                val schemFile = it.get<String>("schematic")
                val file = File("./$schemFile.schem")
                val now = System.currentTimeMillis()
                player.world.placeSchematic {
                    location = player.location
                    schematic = SchematicReader.read(file)
                    then = {
                        val final = System.currentTimeMillis()
                        player.sendMessage("<yellow>Schematic <aqua>${file.name} <yellow>has been placed! Took <pink>${final - now}ms")
                    }
                }
            }
        }
    }


    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}