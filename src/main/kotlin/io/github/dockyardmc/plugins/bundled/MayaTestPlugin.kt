package io.github.dockyardmc.plugins.bundled

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.ServerMetrics
import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.toggle
import io.github.dockyardmc.bossbar.Bossbar
import io.github.dockyardmc.bossbar.BossbarColor
import io.github.dockyardmc.bossbar.BossbarNotches
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.TickPeriod
import io.github.dockyardmc.player.add
import io.github.dockyardmc.plugins.DockyardPlugin
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundEntityEffectPacket
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.runnables.timedSequenceAsync
import io.github.dockyardmc.scroll.RGB
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

        }

        Events.on<PlayerJoinEvent> {
            val effectPacket = ClientboundEntityEffectPacket(it.player, 15, 1, 99999, 0x00)
            it.player.sendPacket(effectPacket)
            it.player.tabListHeader.value = "\n  <dark_gray><s>        <r>  <aqua>DockyardMC<r>  <dark_gray><s>        <r>  \n".toComponent()
            it.player.tabListFooter.value = "\n  <dark_gray><s>                                   <r>  \n".toComponent()
        }

        Commands.add("/bar") {
            it.execute { exec ->
                val bar = Bossbar("<yellow> hello chat :3", 1f, BossbarColor.YELLOW, BossbarNotches.SIX)
                bar.viewers.add(exec.player!!)
            }
        }

        Commands.add("/listed") {
            it.execute { exec ->
                val player = exec.player!!
                player.isListed.toggle()
                player.sendMessage("<yellow>Toggled your tablist listing!")
            }
        }
    }

    override fun unload(server: DockyardServer) {
        TODO("Not yet implemented")
    }
}