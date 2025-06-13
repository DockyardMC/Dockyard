import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.data.components.ConsumableComponent
import io.github.dockyardmc.data.components.ItemModelComponent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.types.ItemRarity
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.scroll.CustomColor
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
        player.give(Items.OAK_LOG.toItemStack().withMeta {
            withEnchantmentGlint(true)
            withDyedColor(CustomColor.fromHex("#ff0000"))
            withDisplayName("<red><u>Crazy Oak Log")
            withRarity(ItemRarity.EPIC)
            withLore("test", "test2", "test3")
            withConsumable(1.3f, ConsumableComponent.Animation.EAT, Sounds.ITEM_MACE_SMASH_GROUND, true)
            withFood(2, 0f, true)
            withCustomModelData(listOf(1f))
            withMaxStackSize(99)
            withComponent(ItemModelComponent("minecraft:birch_log"))
            withGlider()
        })
    }

    server.start()
}