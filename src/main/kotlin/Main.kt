import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.attributes.AttributeModifier
import io.github.dockyardmc.attributes.AttributeOperation
import io.github.dockyardmc.attributes.EquipmentSlotGroup
import io.github.dockyardmc.attributes.Modifier
import io.github.dockyardmc.data.DataComponentHasher
import io.github.dockyardmc.data.components.AttributeModifiersComponent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Attributes
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
        DebugSidebar.sidebar.viewers.add(player)
        player.permissions.add("*")
    }

    val component = AttributeModifiersComponent(listOf(Modifier(Attributes.SCALE, AttributeModifier("minecraft:ass", 1.5, AttributeOperation.ADD), EquipmentSlotGroup.ANY)))

    val hash = DataComponentHasher.hash(component)

    log("dockyard transcoder - $hash")

    server.start()
}