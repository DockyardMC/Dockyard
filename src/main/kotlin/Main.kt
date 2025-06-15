import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.systems.GameMode

fun main() {

    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)
    }

    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.permissions.add("*")
        player.gameMode.value = GameMode.CREATIVE
    }

    Commands.add("/test") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
        }
    }

    server.start()
}