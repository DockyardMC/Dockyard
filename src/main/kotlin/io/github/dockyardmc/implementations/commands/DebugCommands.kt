package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.WorldArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.extentions.truncate
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.profiler.profiler
import io.github.dockyardmc.server.ServerMetrics
import io.github.dockyardmc.utils.DataSizeCounter
import io.github.dockyardmc.world.World

object DebugCommands {

    fun register() {

        Commands.add("/debug_world") {
            addOptionalArgument("world", WorldArgument())
            execute { ctx ->
                val world = getArgumentOrNull<World>("world") ?: ctx.getPlayerOrThrow().world

                ctx.sendMessage(" ")
                ctx.sendMessage(" <gray>Entities:")
                ctx.sendMessage("  <dark_gray>◾ <gray>Total: <white>${world.entities.size}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Tickable: <white>${world.entities.filter { entity -> entity.tickable }.size}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Non-tickable: <white>${world.entities.filter { entity -> !entity.tickable }.size}")
                ctx.sendMessage("  <dark_gray>◾ <gray>AutoViewable: <white>${world.entities.filter { entity -> entity.autoViewable }.size}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Non-AutoViewable: <white>${world.entities.filter { entity -> !entity.autoViewable }.size}")
                ctx.sendMessage(" ")
                ctx.sendMessage(" <gray>Chunks:")
                ctx.sendMessage("  <dark_gray>◾ <gray>Total: <white>${world.chunks.size}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Visible: <white>${world.chunks.filter { chunk -> chunk.value.viewers.isNotEmpty() }.size}")
                ctx.sendMessage(" ")
                ctx.sendMessage(" <gray>Scheduler:")
                ctx.sendMessage("  <dark_gray>◾ <gray>Running: <white>${!world.scheduler.paused.value}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Tickrate: <white>${world.scheduler.tickRate.value.inWholeMilliseconds}ms <gray>(${world.scheduler.mspt} mspt)")
                ctx.sendMessage("  <dark_gray>◾ <gray>Tasks: <white>${world.scheduler.taskSize}")
                ctx.sendMessage(" ")
                ctx.sendMessage(" <gray>World:")
                ctx.sendMessage("  <dark_gray>◾ <gray>Event pool size: <white>${world.eventPool.eventList().size}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Bindable pool size: <white>${world.bindablePool.size}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Loaded: <white>${world.isLoaded.value}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Player Queue: <white>${world.playerJoinQueue.size}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Data Blocks: <white>${world.customDataBlocks.size}")
                ctx.sendMessage(" ")
            }
        }

        Commands.add("/debug_events") {
            execute { ctx ->
                ctx.sendMessage(" ")
                ctx.sendMessage(Events.debugTree())
                ctx.sendMessage(" ")
            }
        }

        Commands.add("/forcegc") {
            execute { ctx ->
                val ms = profiler("Force Collect GC") {
                    System.gc()
                }
                ctx.sendMessage("<gray>Ran GC in <white>${ms}<gray>ms")
            }
        }

        Commands.add("/debug_network") {
            execute { ctx ->
                val dockyard = DockyardServer.instance.nettyServer
                val nettyThreadBoss = dockyard.bossGroup.executorCount()
                val nettyThreadWorker = dockyard.workerGroup.executorCount()

                ctx.sendMessage(" ")
                ctx.sendMessage(" <gray>Netty:")
                ctx.sendMessage("  <dark_gray>◾ <gray>Boss: <white>$nettyThreadBoss")
                ctx.sendMessage("  <dark_gray>◾ <gray>Worker: <white>$nettyThreadWorker")
                ctx.sendMessage("  <dark_gray>◾ <gray>Connections: <white>${PlayerManager.players.size}")
                ctx.sendMessage(" ")
                ctx.sendMessage(" <gray>Network")
                ctx.sendMessage("  <dark_gray>◾ <gray>Packets/Sec: <white>↑${ServerMetrics.packetsSentAverage} ↓${ServerMetrics.packetsReceivedAverage}")
                ctx.sendMessage("  <dark_gray>◾ <gray>Bandwidth: <white>↑${ServerMetrics.outboundBandwidth.getSize(DataSizeCounter.Type.MEGABYTE)}mb ↓${ServerMetrics.inboundBandwidth.getSize(DataSizeCounter.Type.MEGABYTE)}mb")
                ctx.sendMessage(" ")
            }
        }

        Commands.add("/debug_memory") {
            execute { ctx ->
                val runtime = Runtime.getRuntime()
                val message = buildString {
                    append("\n")
                    appendLine(" <gray>Memory Stats:")
                    appendLine(" <dark_gray>◾ <gray>Max memory: <white>${runtime.maxMemory() / 1024 / 1024}MB")
                    appendLine(" <dark_gray>◾ <gray>Total memory: <white>${runtime.totalMemory() / 1024 / 1024}MB")
                    appendLine(" <dark_gray>◾ <gray>Free memory: <white>${runtime.freeMemory() / 1024 / 1024}MB")
                    appendLine(" <dark_gray>◾ <gray>Used memory: <white>${(runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024}MB")
                    appendLine("")
                    appendLine(" <gray>GC Stats:")
                    appendLine(" <dark_gray>◾ <gray>Available processors: <white>${runtime.availableProcessors()}")
                    appendLine(" <dark_gray>◾ <gray>Memory usage: <white>${ServerMetrics.memoryUsagePercent.truncate(1)}%")
                }
                ctx.sendMessage(message)
            }
        }

        Commands.add("/reloadchunks") {
            execute { ctx ->
                val player = ctx.getPlayerOrThrow()
                player.chunkViewSystem.lock.lock()
                player.chunkViewSystem.resendChunks()
                player.chunkViewSystem.lock.unlock()
                player.chunkViewSystem.update()
                player.sendMessage("<gray>Reloaded your chunks!")
            }
        }

        Commands.add("/reloadviewers") {
            execute { ctx ->
                val player = ctx.getPlayerOrThrow()
                player.entityViewSystem.lock.lock()
                player.entityViewSystem.clear()
                player.entityViewSystem.lock.unlock()
                player.entityViewSystem.tick()
                player.sendMessage("<gray>Reloaded your viewers!")
            }
        }
    }
}