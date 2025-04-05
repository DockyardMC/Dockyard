import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.DebugSidebar


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
        DebugSidebar.sidebar.viewers.add(player)
    }

    Commands.add("/test") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()

            val diamond = ItemStack(Items.DIAMOND).withConsumable(1f)
            val coolSword = ItemStack(Items.NETHERITE_SWORD).withDisplayName("<red><bold><u>COOL ASS SWORD<r>").withLore("", "<gray>This is very special sword yep!", "").withMaxStackSize(256)
            player.give(diamond)
            player.give(coolSword)
        }
    }

    server.start()
}