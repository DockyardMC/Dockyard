package io.github.dockyard.tests.inventory

import cz.lukynka.prettylog.log
import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerPickItemFromBlockEvent
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPickItemFromBlockPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.world.WorldManager
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InventoryPickBlockTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
        PlayerTestUtil.getOrCreateFakePlayer().clearInventory()
        WorldManager.mainWorld.setBlock(Vector3(0), Blocks.STONE)
        WorldManager.mainWorld.setBlock(Vector3(1), Blocks.STONE)   }

    @AfterTest
    fun cleanup() {
        PlayerTestUtil.getOrCreateFakePlayer().clearInventory()
        WorldManager.mainWorld.setBlock(Vector3(0), Blocks.STONE)
        WorldManager.mainWorld.setBlock(Vector3(1), Blocks.STONE)
    }

    @Test
    fun testPickItemFromBlock() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        var countdownLatch = CountDownLatch(1)
        val diamondBlock = ItemStack(Items.DIAMOND_BLOCK)
        val netheriteBlock = ItemStack(Items.NETHERITE_BLOCK)

        WorldManager.mainWorld.setBlock(Location(0, 0, 0, WorldManager.mainWorld), Blocks.DIAMOND_BLOCK)
        WorldManager.mainWorld.setBlock(Location(1, 1, 1, WorldManager.mainWorld), Blocks.NETHERITE_BLOCK)

        player.gameMode.value = GameMode.SURVIVAL
        player.clearInventory()
        player.inventory[0] = diamondBlock
        player.inventory[20] = netheriteBlock

        player.heldSlotIndex.value = 8

        var eventListener = Events.on<PlayerPickItemFromBlockEvent> { event ->
            countdownLatch.countDown()
        }

        PlayerTestUtil.sendPacket(player, ServerboundPickItemFromBlockPacket(Vector3(0), false))

        assertTrue(countdownLatch.await(5L, TimeUnit.SECONDS))
        assertEquals(0, player.heldSlotIndex.value)
        assertEquals(diamondBlock, player.getHeldItem(PlayerHand.MAIN_HAND))

        Events.unregister(eventListener)
        countdownLatch = CountDownLatch(1)

        eventListener = Events.on<PlayerPickItemFromBlockEvent> { event ->
            countdownLatch.countDown()
        }

        PlayerTestUtil.sendPacket(player, ServerboundPickItemFromBlockPacket(Vector3(1), false))

        assertTrue(countdownLatch.await(5L, TimeUnit.SECONDS))
        assertEquals(0, player.heldSlotIndex.value)
        assertEquals(player.inventory[20], diamondBlock)
        assertEquals(netheriteBlock, player.getHeldItem(PlayerHand.MAIN_HAND))

        Events.unregister(eventListener)
    }
}