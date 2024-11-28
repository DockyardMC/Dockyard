package io.github.dockyard.tests.block

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerStartDiggingBlockEvent
import io.github.dockyardmc.item.EquipmentSlot
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.play.serverbound.PlayerAction
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerActionPacket
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundUseItemOnBlockPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.world.WorldManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerBlockActionsTests {

    val pool = EventPool()

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
        WorldManager.mainWorld.setBlock(0, 1, 0, Block.AIR)
        PlayerTestUtil.getOrCreateFakePlayer().mainHandItem.value = ItemStack.AIR
        PlayerTestUtil.getOrCreateFakePlayer().gameMode.value = GameMode.SURVIVAL
        pool.dispose()
    }

    @Test
    fun testPlayerPlaceBlock() {

        val pos = Vector3(0, 1, 0)
        val block = Blocks.AMETHYST_BLOCK
        val item = Items.AMETHYST_BLOCK

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.equipment[EquipmentSlot.MAIN_HAND] = item.toItemStack()

        val packet = ServerboundUseItemOnBlockPacket(PlayerHand.MAIN_HAND, Vector3(0, 0, 0), Direction.UP, 0f, 0f, 0f, false, false, 1)
        PlayerTestUtil.sendPacket(player, packet)

        assertEquals(block, player.world.getBlock(pos).registryBlock)
    }

    @Test
    fun testPlayerStartDigging() {

        val pos = Vector3(0, 1, 0)
        val block = Blocks.DECORATED_POT

        pool.on<PlayerStartDiggingBlockEvent> {
            assertEquals(block, it.block.registryBlock)
            assertEquals(pos, it.location.toVector3())
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()

        WorldManager.mainWorld.setBlock(0, 1, 0, block)

        val packet = ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, pos, Direction.UP, 0)
        PlayerTestUtil.sendPacket(player, packet)
    }

    @Test
    fun testPlayerBreakBlockCreative() {

        val pos = Vector3(0, 1, 0)
        val block = Blocks.DECORATED_POT

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.gameMode.value = GameMode.CREATIVE

        WorldManager.mainWorld.setBlock(0, 1, 0, block)

        val packet = ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, pos, Direction.UP, 0)
        PlayerTestUtil.sendPacket(player, packet)

        assertEquals(Blocks.AIR, player.world.getBlock(pos).registryBlock)
    }


    @Test
    fun testPlayerBreakBlockSurvival() {

        val pos = Vector3(0, 1, 0)
        val block = Blocks.DECORATED_POT

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.gameMode.value = GameMode.SURVIVAL

        WorldManager.mainWorld.setBlock(0, 1, 0, block)

        val packet = ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, pos, Direction.UP, 0)
        PlayerTestUtil.sendPacket(player, packet)

        assertEquals(block, player.world.getBlock(pos).registryBlock)
    }
}