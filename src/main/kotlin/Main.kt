import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.Warden
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.playSound

fun main() {

    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)
    }

    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.gameMode.value = GameMode.CREATIVE
    }

    Commands.add("/warden") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            val warden = player.world.spawnEntity(Warden(player.location)) as Warden
        }
    }

    Commands.add("/alert") {
        execute { ctx ->
            val location = ctx.getPlayerOrThrow().location
            location.world.playSound(Sounds.BLOCK_SCULK_SHRIEKER_SHRIEK, location)
            location.world.entities.filter { entity -> entity.location.distance(location) < 30.0 }.filterIsInstance<Warden>().forEach { warden ->
                warden.behaviourController.heardSoundInvestigationLocation = location.closestSolidBelow?.second
            }
        }
    }

    server.start()
}