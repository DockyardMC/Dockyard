import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.ui.new.CookieClickerScreen
import io.github.dockyardmc.utils.DebugSidebar

fun main() {
    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)
    }

    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.gameMode.value = GameMode.CREATIVE
        DebugSidebar.sidebar.addViewer(player)
        player.permissions.add("*")
        player.give(Items.DEBUG_STICK)
        player.give(Items.OAK_LOG)
    }

    Commands.add("/lighting") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            player.strikeLightning(player.location)
        }
    }

    Commands.add("/ui") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            val screen = CookieClickerScreen()
            screen.open(player)
            screen.render(player)
        }
    }

    server.start()
}