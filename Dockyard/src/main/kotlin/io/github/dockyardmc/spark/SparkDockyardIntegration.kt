package io.github.dockyardmc.spark

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.LogType
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.CommandExecutor
import io.github.dockyardmc.extentions.sendMessage
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.Console
import io.github.dockyardmc.utils.Disposable
import me.lucko.spark.common.SparkPlatform
import me.lucko.spark.common.SparkPlugin
import me.lucko.spark.common.command.sender.CommandSender
import me.lucko.spark.common.monitor.ping.PlayerPingProvider
import me.lucko.spark.common.platform.PlatformInfo
import me.lucko.spark.common.tick.TickHook
import me.lucko.spark.common.tick.TickReporter
import java.nio.file.Path
import java.util.logging.Level
import java.util.stream.Stream

class SparkDockyardIntegration : SparkPlugin, Disposable {

    lateinit var platform: SparkPlatform

    companion object {
        val LOG_TYPE = CustomLogType("⚡ Spark", AnsiPair.PURPLE_ISH_BLUE)
    }

    fun initialize(): SparkPlatform {
        platform = SparkPlatform(this)
        platform.enable()

        registerCommand()

        return platform
    }

    override fun log(level: Level, message: String) {
        val type = if (level.intValue() >= 1000) LogType.CRITICAL else if (level.intValue() >= 900) LogType.WARNING else LOG_TYPE
        cz.lukynka.prettylog.log(message, type)
    }

    override fun log(level: Level, message: String, throwable: Throwable) {
        cz.lukynka.prettylog.log(throwable as Exception)
    }

    override fun getVersion(): String {
        return "1.10.119"
    }

    override fun getPluginDirectory(): Path {
        return Path.of("./spark/")
    }

    override fun getCommandName(): String {
        return "spark"
    }

    override fun getCommandSenders(): Stream<out CommandSender> {
        return Stream.concat(
            PlayerManager.players.map { player -> SparkCommandSender(CommandExecutor(player, Console, "", true)) }.stream(),
            Stream.of(SparkCommandSender(CommandExecutor(null, Console, "")))
        )
    }

    override fun executeAsync(runnable: Runnable) {
        DockyardServer.scheduler.runAsync { runnable.run() }
    }

    override fun getPlatformInfo(): PlatformInfo {
        return SparkPlatformInfo()
    }

    override fun createTickReporter(): TickReporter {
        return SparkTickReporter()
    }

    override fun createPlayerPingProvider(): PlayerPingProvider {
        return SparkPingProvider()
    }

    override fun createTickHook(): TickHook {
        return SparkTickHook()
    }

    override fun dispose() {
        platform.disable()
    }

    fun registerCommand() {
        SparkCommand(this).register()
    }

    private val prefix = "<dark_gray>[<yellow><bold>⚡</bold><dark_gray>]<gray>"

    fun prefixed(message: String): String {
        return "$prefix $message"
    }

    fun broadcastPrefixed(message: String) {
        PlayerManager.players.filter { player -> player.hasPermission("spark.use") }.sendMessage("$prefix $message")
    }

    fun broadcastPrefixed(component: Component) {
        if (component.color == null) component.apply { color = LegacyTextColor.GRAY.hex }
        PlayerManager.players.filter { player -> player.hasPermission("spark.use") }.sendMessage(Component.compound(mutableListOf("$prefix <gray>".toComponent(), component)))
    }
}