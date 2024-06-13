package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.ServerMetrics
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEffectPacket
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Vector3f

class MayaTestPlugin: DockyardPlugin {
    override var name: String = "MayaTestPlugin"
    override var author: String = "LukynkaCZE"
    override var version: String = DockyardServer.versionInfo.dockyardVersion

    override fun load(server: DockyardServer) {

        Period.on<TickPeriod> {
//            val runtime = Runtime.getRuntime()
//            val mspt = ServerMetrics.millisecondsPerTick
//            val memoryUsage = runtime.totalMemory() - runtime.freeMemory()
//            val memUsagePercent = MathUtils.percent(runtime.totalMemory().toDouble(), memoryUsage.toDouble()).truncate(0)
//
//            val fMem = (memoryUsage.toDouble() / 1000000).truncate(1)
//            val fMax = (runtime.totalMemory().toDouble() / 1000000).truncate(1)
//            DockyardServer.broadcastActionBar("<white>MSPT: <lime>$mspt <dark_gray>| <white>Memory Usage: <#ff6830>$memUsagePercent% <gray>(${fMem}mb / ${fMax}mb)")
            PlayerManager.players.forEach {
                it.world.spawnParticle(it.location.apply { y += 1.5 }, Particles.END_ROD, offset = Vector3f(0f), speed = 0f)
            }
        }

        Events.on<PlayerJoinEvent> {
            val packet = ClientboundEntityEffectPacket(it.player, 15, 1, 99999, 0x00)
            it.player.sendPacket(packet)
        }
    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}