package io.github.dockyardmc.spark.command

import io.github.dockyardmc.commands.Command
import io.github.dockyardmc.commands.CommandExecutor
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.spark.SparkDockyardIntegration


class PingSubCommand(val spark: SparkDockyardIntegration) : SparkSubCommand {


    override fun register(command: Command) {

        Commands.add("ping") {
            addOptionalArgument("player", PlayerArgument())
            withDescription("Shows ping of specified player")
            execute { ctx ->
                val player = getArgumentOrNull<Player>("player") ?: ctx.getPlayerOrThrow()
                handlePing(ctx, player)
            }
        }

        command.addSubcommand("ping") {
            addOptionalArgument("player", PlayerArgument())
            execute { ctx ->
                val player = getArgumentOrNull<Player>("player") ?: ctx.getPlayerOrThrow()
                handlePing(ctx, player)
            }
        }
    }

    private fun handlePing(ctx: CommandExecutor, player: Player) {
        player.sendPingRequest().thenAccept { ping ->
            val message = if (ctx.player != null && ctx.player == player) "Your ping is:" else "${player.username}'s ping is:"
            ctx.sendMessage(spark.prefixed("$message ${formatPing(ping)}"))
        }
    }

    private fun formatPing(ping: Long): String {

        val color = when {
            ping <= 20 -> "<lime>"
            ping <= 50 -> "<yellow>"
            ping <= 80 -> "<orange>"
            else -> "<red>"
        }

        return "${color}${ping}ms"
    }
}