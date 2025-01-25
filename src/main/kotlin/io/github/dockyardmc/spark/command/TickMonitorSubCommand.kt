package io.github.dockyardmc.spark.command

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.scroll.serializers.KyoriToScrollSerializer.toScroll
import io.github.dockyardmc.spark.SparkDockyardIntegration
import io.github.dockyardmc.spark.SparkWorldSpecificTickHook
import io.github.dockyardmc.world.World
import me.lucko.spark.common.monitor.tick.ReportPredicate
import me.lucko.spark.common.monitor.tick.TickMonitor
import me.lucko.spark.common.tick.TickHook
import net.kyori.adventure.text.Component

class TickMonitorSubCommand(val spark: SparkDockyardIntegration) : SparkSubCommand {

    private val platform = spark.platform
    private var activeTickMonitor: ReportingTickMonitor? = null
    private var tickHook: TickHook? = null

    override fun register(command: Command) {

        command.addSubcommand("tickmonitor") {
            addArgument("type", EnumArgument(TickMonitorType::class))
            addOptionalArgument("world", WorldArgument())
            execute { ctx ->
                val type = getEnumArgument<TickMonitorType>("type")
                val world = getArgumentOrNull<World>("world")

                if (type == TickMonitorType.STOP) {
                    if (activeTickMonitor == null) throw CommandException("There is no active tick monitor")

                    activeTickMonitor!!.close()
                    if (this@TickMonitorSubCommand.tickHook is SparkWorldSpecificTickHook) this@TickMonitorSubCommand.tickHook?.close()
                    activeTickMonitor = null

                    spark.broadcastPrefixed("<red>Tick monitor disabled.")
                    return@execute
                }

                val tickHook = when (type) {
                    TickMonitorType.WORLD -> {
                        if (world == null) throw CommandException("You need to provide a world when using world tick monitor!")
                        val tickHook = SparkWorldSpecificTickHook(world)
                        tickHook.start()
                        tickHook
                    }

                    TickMonitorType.GLOBAL -> platform.tickHook
                    else -> platform.tickHook
                }
                this@TickMonitorSubCommand.tickHook = tickHook

                if (tickHook == null) {
                    ctx.sendMessage(spark.prefixed("<red>Not Supported!"))
                    return@execute
                }

                val messageType = if (type == TickMonitorType.WORLD) "world specific (<white>${world!!.name}<gray>)" else "<white>global"
                spark.broadcastPrefixed("Starting $messageType tick monitor..")

                val reportPredicate = ReportPredicate.PercentageChangeGt(100.0)
                activeTickMonitor = ReportingTickMonitor(tickHook, reportPredicate, spark, false)
                activeTickMonitor!!.start()
            }
        }
    }

    enum class TickMonitorType {
        WORLD,
        GLOBAL,
        STOP
    }

    private class ReportingTickMonitor(tickHook: TickHook, reportPredicate: ReportPredicate, val spark: SparkDockyardIntegration, gc: Boolean) : TickMonitor(spark.platform, tickHook, reportPredicate, gc) {

        override fun sendMessage(message: Component) {
            val dockyardComp = message.toScroll()
            spark.broadcastPrefixed(dockyardComp)
        }
    }
}