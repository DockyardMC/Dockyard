package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.ServerMetrics
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.utils.MathUtils

class MayaTestPlugin: DockyardPlugin {
    override var name: String = "MayaTestPlugin"
    override var author: String = "LukynkaCZE"
    override var version: String = DockyardServer.versionInfo.dockyardVersion

    override fun load(server: DockyardServer) {

        Period.on<TickPeriod> {
            val runtime = Runtime.getRuntime()
            val mspt = ServerMetrics.millisecondsPerTick
            val memoryUsage = runtime.totalMemory() - runtime.freeMemory()
            val memUsagePercent = MathUtils.percent(runtime.totalMemory().toDouble(), memoryUsage.toDouble()).truncate(0)

            val fMem = (memoryUsage.toDouble() / 1000000).truncate(1)
            val fMax = (runtime.totalMemory().toDouble() / 1000000).truncate(1)
            DockyardServer.broadcastActionBar("<white>MSPT: <lime>$mspt <dark_gray>| <white>Memory Usage: <#ff6830>$memUsagePercent% <gray>(${fMem}mb / ${fMax}mb)")
//                PlayerManager.players.filter { it.getProcessor() != null && it.getProcessor()!!.state == ProtocolState.PLAY }.forEach { it.spawnParticle(it.location, Particles.FLAME, speed = 0f) }
        }

        Commands.add("/skin") {
            it.execute { exec ->
                exec.player!!.updateSkin()
                exec.player.updateDisplayedSkinParts()
            }
        }

        Commands.add("/boom") {
            it.permission = "commands.troll"
            it.addArgument("target", PlayerArgument())
            it.execute { exec ->
                if(!exec.isPlayer) exec.console.sendMessage("<red>Only players can execut this command!")
                val player = exec.player!!
                val target = it.get<Player>("target")
                player.spawnParticle(target.location, Particles.EXPLOSION_EMITTER, speed = 0f, count = 3)
                player.spawnParticle(target.location, Particles.SMOKE, speed = 0.2f, count = 10)
                player.spawnParticle(target.location, Particles.FLAME, speed = 0.2f, count = 10)
                target.sendMessage("<red>you got totally exploded by <yellow>$player<red>!!!")
            }
        }
    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}