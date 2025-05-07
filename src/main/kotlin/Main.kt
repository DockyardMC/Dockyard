import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.attributes.AttributeModifier
import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashStruct
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.sounds.CustomSoundEvent
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

//    val component = AttributeModifiersComponent(listOf(Modifier(Attributes.SCALE, AttributeModifier("minecraft:test", 1.5, AttributeOperation.ADD_VALUE), EquipmentSlotGroup.FEET)))
    val component = CustomSoundEvent("minecraft:gay")

    val hash = component.hashStruct().getHashed()

    log("dockyard transcoder - $hash")

    server.start()
}

//hasher object
// normal -> writes map to the hasher
// inline -> writes directly to the hasher

data class TestThing(val modifier: AttributeModifier, val test: String) : DataComponentHashable {
    override fun hashStruct(): HashStruct {
        return CRC32CHasher.of {
            inline(modifier.hashStruct())
            static("test", CRC32CHasher.ofString(test))
        }
    }
}