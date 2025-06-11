import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.DebugSidebar

fun main() {

    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
        useDebugMode(true)
        useMojangAuth(false)
    }

    Events.on<PlayerJoinEvent> { event ->
        val player = event.player
        player.gameMode.value = GameMode.CREATIVE
        DebugSidebar.sidebar.viewers.add(player)
        player.permissions.add("*")
        player.give(Items.OAK_LOG.toItemStack().withMeta { withEnchantmentGlint(true) })
    }

    server.start()
}