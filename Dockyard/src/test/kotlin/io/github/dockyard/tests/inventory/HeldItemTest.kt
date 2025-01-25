package io.github.dockyard.tests.inventory

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.serverbound.PlayerAction
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerActionPacket
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundSetPlayerHeldItemPacket
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.vectors.Vector3
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HeldItemTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
        PlayerTestUtil.sendPacket(PlayerTestUtil.getOrCreateFakePlayer(), ServerboundSetPlayerHeldItemPacket(0))
    }

    @AfterTest
    fun cleanup() {
        PlayerTestUtil.getOrCreateFakePlayer().clearInventory()
        PlayerTestUtil.sendPacket(PlayerTestUtil.getOrCreateFakePlayer(), ServerboundSetPlayerHeldItemPacket(0))
    }

    @Test
    fun testHeldItem() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        player.inventory[0] = ItemStack.AIR
        player.inventory[1] = ItemStack(Items.DIAMOND_SWORD)

        assertEquals(ItemStack.AIR, player.getHeldItem(PlayerHand.MAIN_HAND))
        assertEquals(ItemStack.AIR, player.mainHandItem)
        assertEquals(0, player.heldSlotIndex.value)

        PlayerTestUtil.sendPacket(player, ServerboundSetPlayerHeldItemPacket(1))

        assertEquals(ItemStack(Items.DIAMOND_SWORD), player.getHeldItem(PlayerHand.MAIN_HAND))
        assertEquals(ItemStack(Items.DIAMOND_SWORD), player.mainHandItem)
        assertEquals(1, player.heldSlotIndex.value)

        player.clearInventory()

        assertEquals(ItemStack.AIR, player.getHeldItem(PlayerHand.MAIN_HAND))
        assertEquals(ItemStack.AIR, player.mainHandItem)
        assertEquals(1, player.heldSlotIndex.value)
    }

    @Test
    fun testSetHeldItem() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        player.inventory[0] = ItemStack.AIR

        assertEquals(ItemStack.AIR, player.getHeldItem(PlayerHand.MAIN_HAND))
        assertEquals(ItemStack.AIR, player.mainHandItem)

        player.inventory[0] = ItemStack(Items.COOKIE)

        assertEquals(ItemStack(Items.COOKIE), player.getHeldItem(PlayerHand.MAIN_HAND))
        assertEquals(ItemStack(Items.COOKIE), player.mainHandItem)
    }

    @Test
    fun testOffhand() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        player.inventory[0] = ItemStack(Items.DIAMOND_SWORD)
        PlayerTestUtil.sendPacket(player, ServerboundSetPlayerHeldItemPacket(0))

        assertEquals(ItemStack(Items.DIAMOND_SWORD), player.getHeldItem(PlayerHand.MAIN_HAND))
        assertEquals(ItemStack(Items.DIAMOND_SWORD), player.mainHandItem)

        PlayerTestUtil.sendPacket(player, ServerboundPlayerActionPacket(PlayerAction.SWAP_ITEM, Vector3(0), Direction.DOWN, 0))

        assertEquals(ItemStack.AIR, player.getHeldItem(PlayerHand.MAIN_HAND))
        assertEquals(ItemStack.AIR, player.mainHandItem)

        assertEquals(ItemStack(Items.DIAMOND_SWORD), player.getHeldItem(PlayerHand.OFF_HAND))
        assertEquals(ItemStack(Items.DIAMOND_SWORD), player.offHandItem)
    }
}