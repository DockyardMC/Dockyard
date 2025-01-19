package io.github.dockyard.tests

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.inventory.PlayerInventoryUtils
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.play.serverbound.ContainerClickMode
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundClickContainerPacket
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext
import org.mockito.Mockito
import java.util.UUID
import kotlin.test.assertEquals

object PlayerTestUtil {

    var player: Player? = null

    fun getOrCreateFakePlayer(): Player {
        if (player == null) {
            player = Player(
                "LukynkaCZE",
                entityId = EntityManager.entityIdCounter.incrementAndGet(),
                uuid = UUID.fromString("0c9151e4-7083-418d-a29c-bbc58f7c741b"),
                world = WorldManager.mainWorld,
                connection = Mockito.mock<ChannelHandlerContext>(),
                address = "0.0.0.0",
                networkManager = PlayerNetworkManager(),
            )
        }

        PlayerManager.add(player!!, player!!.networkManager)
        player!!.gameMode.value = GameMode.SURVIVAL
        return player!!
    }

    fun sendPacket(player: Player, packet: ServerboundPacket) {
        log("Sent ${packet::class.simpleName} to fake test player", LogType.DEBUG)
        packet.handle(player.networkManager, player.connection, 0, 0)
    }
}

fun sendSlotClick(player: Player, slot: Int, button: Int, mode: ContainerClickMode, itemStack: ItemStack){
    PlayerTestUtil.sendPacket(player, ServerboundClickContainerPacket(0, 0, PlayerInventoryUtils.convertToPacketSlot(slot), button, mode, mutableMapOf(), itemStack))
}

fun assertSlot(player: Player, slot: Int, expected: ItemStack) {
    assertEquals(expected, player.inventory[slot])
}

fun assertSlot(player: Player, slot: Int, expected: Item) {
    assertSlot(player, slot, expected.toItemStack())
}