package io.github.dockyard.tests.block.handlers

import cz.lukynka.prettylog.log
import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockPlaceEvent
import io.github.dockyardmc.events.PlayerFinishPlacingBlockEvent
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundUseItemOnBlockPacket
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.block.Block
import java.util.concurrent.CountDownLatch

object BlockHandlerTestUtil {

    fun reset() {
        WorldManager.mainWorld.setBlock(0, 0, 0, Blocks.AIR)
    }

    fun place(item: Item, face: Direction, clickLocation: Vector3, cursor: Vector3f): Block {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.setHeldItem(PlayerHand.MAIN_HAND, item.toItemStack(1))

        val latch = CountDownLatch(1)
        val packet = ServerboundUseItemOnBlockPacket(PlayerHand.MAIN_HAND, clickLocation, face, cursor.x, cursor.y, cursor.z, false, hitWorldBorder = false, sequence = 0)

        val listener = Events.on<PlayerFinishPlacingBlockEvent> { event ->
            latch.countDown()
        }

        PlayerTestUtil.sendPacket(PlayerTestUtil.getOrCreateFakePlayer(), packet)
        latch.await()
        Events.unregister(listener)
        return WorldManager.mainWorld.getBlock(0, 0, 0)
    }
}