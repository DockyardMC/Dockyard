package io.github.dockyardmc.spark.command

import io.github.dockyardmc.commands.Command
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.spark.SparkDockyardIntegration
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo
import me.lucko.spark.common.monitor.cpu.CpuMonitor
import me.lucko.spark.common.util.FormatUtil
import java.util.*
import kotlin.math.min

class TpsSubCommand(val spark: SparkDockyardIntegration): SparkSubCommand {

    val platform get() = spark.platform

    override fun register(command: Command) {

        Commands.add("/tps") {
            withPermission("spark.command")
            withDescription("Shows TPS, Tick durations and CPU usage of the server")
            execute { ctx ->
                val player = ctx.getPlayerOrThrow()
                player.runCommand("spark tps")
            }
        }

        command.addSubcommand("tps") {
            execute { ctx ->
                val tickStatistics = platform.tickStatistics
                if(tickStatistics != null) {
                    ctx.sendMessage(" ")
                    ctx.sendMessage(spark.prefixed("TPS from last 5s, 10s, 1m, 5m, 15m:"))
                    ctx.sendMessage(spark.prefixed("${formatTps(tickStatistics.tps5Sec())}, ${formatTps(tickStatistics.tps10Sec())}, ${formatTps(tickStatistics.tps1Min())}, ${formatTps(tickStatistics.tps5Min())}, ${formatTps(tickStatistics.tps15Min())}"))
                    ctx.sendMessage(" ")

                    if(tickStatistics.isDurationSupported) {
                        ctx.sendMessage(spark.prefixed("Tick durations (min/med/95%ile/max ms) from last 10s, 1m:"))
                        ctx.sendMessage(spark.prefixed("${formatTickDuration(tickStatistics.duration10Sec())};  ${formatTickDuration(tickStatistics.duration1Min())}"))
                        ctx.sendMessage(" ")
                    }
                }

                ctx.sendMessage(spark.prefixed("CPU usage from last 10s, 1m, 15m:"))
                ctx.sendMessage(spark.prefixed("${formatCpuUsage(CpuMonitor.systemLoad10SecAvg())}, ${formatCpuUsage(CpuMonitor.systemLoad1MinAvg())}, ${formatCpuUsage(CpuMonitor.systemLoad15MinAvg())} <dark_gray>(system)"))
                ctx.sendMessage(spark.prefixed("${formatCpuUsage(CpuMonitor.processLoad10SecAvg())}, ${formatCpuUsage(CpuMonitor.processLoad1MinAvg())}, ${formatCpuUsage(CpuMonitor.processLoad15MinAvg())} <dark_gray>(process)"))
            }
        }
    }

    private fun formatTps(tps: Double): String {
        val color = if(tps > 18.0) {
            "<lime>"
        } else if(tps > 16.0) {
            "<yellow>"
        } else {
            "<red>"
        }

        return "$color${if(tps > 20.0) "*" else ""}${min(Math.round(tps * 100.0) / 100.0, 20.0)}<gray>"
    }

    private fun formatTickDuration(average: DoubleAverageInfo): String {
        return "${formatTickDuration(average.min())}/${formatTickDuration(average.median())}/${formatTickDuration(average.percentile95th())}/${formatTickDuration(average.max())}"
    }

    private fun formatTickDuration(duration: Double): String {
        val color = if(duration >= 50) {
            "<red>"
        } else if(duration >= 40) {
            "<yellow>"
        } else {
            "<lime>"
        }

        return "$color${String.format(Locale.ENGLISH, "%.1f", duration)}<gray>"
    }

    private fun formatCpuUsage(usage: Double): String {
        val color = if(usage > 0.9) {
            "<red>"
        } else if (usage > 0.65) {
            "<yellow>"
        } else {
            "<lime>"
        }

        return "$color${FormatUtil.percent(usage, 1.0)}<gray>"
    }
}