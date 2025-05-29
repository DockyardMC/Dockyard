import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.CommandException
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.ui.CookieClickerScreen
import io.github.dockyardmc.ui.snapshot.InventorySnapshot
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

    Commands.add("/ui") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()
            val screen = CookieClickerScreen()
            screen.open(player)
        }
    }

    var snapshot: InventorySnapshot? = null
    Commands.add("/snapshot") {
        addSubcommand("take") {
            execute { ctx ->
                val player = ctx.getPlayerOrThrow()
                snapshot = InventorySnapshot(player)
                player.sendMessage("<orange>taken inventory snapshot")
            }
        }

        addSubcommand("restore") {
            execute { ctx ->
                val player = ctx.getPlayerOrThrow()
                if(snapshot == null) throw CommandException("There is not inventory snapshot taken")
                snapshot!!.restore()
                player.sendMessage("<lime>Restored inventory snapshot from ${snapshot!!.created.toEpochMilliseconds()}")
            }
        }
    }

    server.start()
}