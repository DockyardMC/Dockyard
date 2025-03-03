package io.github.dockyard.tests.block

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.world.block.Block
import io.github.dockyardmc.events.EventPool
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
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.scheduler.runLaterAsync
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
        WorldManager.generateDefaultStonePlatform(WorldManager.mainWorld, 30)
        WorldManager.mainWorld.setBlock(0, 1, 0, io.github.dockyardmc.world.block.Block.AIR)
        PlayerTestUtil.getOrCreateFakePlayer().mainHandItem = ItemStack.AIR
        PlayerTestUtil.getOrCreateFakePlayer().gameMode.value = GameMode.SURVIVAL
        pool.dispose()
    }

    @Test
    fun testPlayerPlaceBlock() {

        val block = Blocks.AMETHYST_BLOCK
        val item = Items.AMETHYST_BLOCK.toItemStack()
        val air = Blocks.AIR

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val pos = Vector3(0, 1, 0)

        val scenarios = mutableListOf<Pair<GameMode, PlaceBlockOutcome>>(
            GameMode.SURVIVAL to PlaceBlockOutcome(block, startingItemStack = item.withAmount(1), expectedItemStack = ItemStack.AIR),
            GameMode.SURVIVAL to PlaceBlockOutcome(block, startingItemStack = item.withAmount(2), expectedItemStack = item.withAmount(1)),
            GameMode.CREATIVE to PlaceBlockOutcome(block, startingItemStack = item.withAmount(1), expectedItemStack = item.withAmount(1)),
            GameMode.ADVENTURE to PlaceBlockOutcome(air, startingItemStack = item.withAmount(1), expectedItemStack = item.withAmount(1)),
            GameMode.SPECTATOR to PlaceBlockOutcome(air, startingItemStack = item.withAmount(1), expectedItemStack = item.withAmount(1)),
        )

        scenarios.forEach { (mode, outcome) ->
            player.gameMode.value = mode
            player.world.setBlock(pos.toLocation(player.world), Blocks.AIR)
            player.equipment[EquipmentSlot.MAIN_HAND] = outcome.startingItemStack

            val packet = ServerboundUseItemOnBlockPacket(PlayerHand.MAIN_HAND, Vector3(0, 0, 0), Direction.UP, 0f, 0f, 0f, insideBlock = false, hitWorldBorder = false, sequence = 1)
            PlayerTestUtil.sendPacket(player, packet)

            // wait a bit cause world modifying is on different thread
            runLaterAsync(1) {
                log("Testing place block with gamemode ${mode.name}", LogType.DEBUG)
                assertEquals(outcome.expectedBlock, player.world.getBlock(pos).registryBlock)
                assertEquals(outcome.expectedItemStack, player.getHeldItem(PlayerHand.MAIN_HAND))
            }
        }
    }

    data class PlaceBlockOutcome(
        val expectedBlock: RegistryBlock,
        val startingItemStack: ItemStack,
        val expectedItemStack: ItemStack
    )

    @Test
    fun testPlayerBreakBlockTest() {

        val block = Blocks.SNOW
        val air = Blocks.AIR

        val scenarios = mapOf<GameMode, RegistryBlock>(
            GameMode.SURVIVAL to air,
            GameMode.CREATIVE to air,
            GameMode.ADVENTURE to block,
            GameMode.SPECTATOR to block,
        )

        val pos = Vector3(0, 1, 0)

        val player = PlayerTestUtil.getOrCreateFakePlayer()

        scenarios.forEach { (mode, outcomeBlock) ->
            player.gameMode.value = mode
            WorldManager.mainWorld.setBlock(0, 1, 0, block)

            val startPacket = ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, pos, Direction.UP, 0)
            PlayerTestUtil.sendPacket(player, startPacket)

            val endPacket = ServerboundPlayerActionPacket(PlayerAction.FINISHED_DIGGING, pos, Direction.UP, 0)
            PlayerTestUtil.sendPacket(player, endPacket)

            block.destroyTime

            log("Testing block break with gamemode ${mode.name}", LogType.DEBUG)
            assertEquals(outcomeBlock, player.world.getBlock(pos).registryBlock)
        }
    }

    @Test
    fun testPlayerCancelDigging() {
        val pos = Vector3(0, 1, 0)
        val block = Blocks.SNOW

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.gameMode.value = GameMode.SURVIVAL

        WorldManager.mainWorld.setBlock(0, 1, 0, block)

        val startPacket = ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, pos, Direction.UP, 0)
        PlayerTestUtil.sendPacket(player, startPacket)

        val cancelPacket = ServerboundPlayerActionPacket(PlayerAction.CANCELLED_DIGGING, pos, Direction.UP, 0)
        PlayerTestUtil.sendPacket(player, cancelPacket)

        assertEquals(block, player.world.getBlock(pos).registryBlock)
    }
}