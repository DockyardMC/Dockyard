package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import kotlin.time.Duration.Companion.seconds

class EffectCommand {

    private fun suggestEffects(player: Player): List<String> {
        return PotionEffectRegistry.potionEffects.keys.toList()
    }

    init {
        Commands.add("/effect") {
            withPermission("dockyard.commands.effect")

            addSubcommand("give") {
                addArgument("player", PlayerArgument())
                addArgument("effect", PotionEffectArgument(), ::suggestEffects)
                addOptionalArgument("duration", IntArgument())
                addOptionalArgument("amplifier", IntArgument())
                addOptionalArgument("hide_particles", BooleanArgument())

                execute { ctx ->
                    val player = getArgument<Player>("player")
                    val effect = getArgument<PotionEffect>("effect")
                    val time = getArgumentOrNull<Int>("duration")?.seconds ?: 30.seconds
                    val amplifier = getArgumentOrNull<Int>("amplifier") ?: 1
                    val hideParticles = getArgumentOrNull<Boolean>("hide_particles") ?: false

                    player.addPotionEffect(effect, time, amplifier, !hideParticles)
                    ctx.sendMessage("<gray>Applied effect ${effect.name} to $player")
                }
            }

            addSubcommand("clear") {
                addOptionalArgument("player", PlayerArgument())
                addOptionalArgument("effect", StringArgument(), ::suggestEffects)
                execute { ctx ->
                    val player = getArgumentOrNull<Player>("player") ?: ctx.getPlayerOrThrow()
                    val effect = getArgumentOrNull<PotionEffect>("effect")

                    if(player.potionEffects.values.isEmpty()) throw CommandException("Target has no effects ")

                    val message: String

                    if (effect != null) {
                        player.removePotionEffect(effect)
                        message = "Removed effect ${effect.name} from $player"
                    } else {
                        player.clearPotionEffects()
                        message = "Removed every effect from $player"
                    }
                    ctx.sendMessage("<gray>$message")
                }
            }
        }
    }
}