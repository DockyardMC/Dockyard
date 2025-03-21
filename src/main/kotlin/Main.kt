import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.Interaction
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.GameMode
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

    Commands.add("/interaction") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            val interaction = player.world.spawnEntity(Interaction(player.location)) as Interaction
            interaction.width.value = 3f
            interaction.height.value = 3f

            interaction.rightClickDispatcher.register { p ->
                p.sendMessage("<yellow>Right Click")
            }

            interaction.leftClickDispatcher.register { p ->
                p.sendMessage("<yellow>Left Click")
            }

            interaction.middleClickDispatcher.register { p ->
                p.sendMessage("<yellow>Middle Click")
            }
        }
    }

    server.start()
}