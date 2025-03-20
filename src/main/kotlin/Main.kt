import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.attributes.AttributeOperation
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.IntArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Attributes
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.registry.registries.PotionEffectRegistry

fun suggestPotionEffects(player: Player): List<String> {
    return PotionEffectRegistry.potionEffects.keys.toList()
}

fun main() {
    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        withUpdateChecker(false)
        useDebugMode(true)
    }

    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.permissions.add("dockyard.admin")
        player.permissions.add("dockyard.*")
        player.gameMode.value = GameMode.CREATIVE
    }

    Commands.add("/test") {
        addSubcommand("apply") {
            addArgument("effect", StringArgument(), ::suggestPotionEffects)
            addArgument("amplifier", IntArgument())
            execute { ctx ->
                val player = ctx.getPlayerOrThrow()
                val effect = PotionEffectRegistry[getArgument("effect")]
                player.addPotionEffect(effect, -1, getArgument("amplifier"))
            }
        }

        addSubcommand("clear") {
            execute { ctx ->
                val player = ctx.getPlayerOrThrow()
                player.clearPotionEffects()
            }
        }

        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            player.attributes[Attributes.MOVEMENT_SPEED].addModifier("test", 0.2, AttributeOperation.ADD)
        }
    }

    server.start()
}