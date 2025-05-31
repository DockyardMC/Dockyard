import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerRightClickWithItemEvent
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.ui.TestScreen
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

    Events.on<PlayerRightClickWithItemEvent> { event ->
        event.player.setVelocity(Vector3d(0, 20.5, 0))
    }

    Commands.add("/ui") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            val screen = TestScreen()
            screen.open(player)
        }
    }

    Commands.add("/test") {
        execute { ctx ->
            ctx.sendMessage("gay ass ")
            ctx.sendMessage("asd ass ")
        }
    }

    server.start()
}