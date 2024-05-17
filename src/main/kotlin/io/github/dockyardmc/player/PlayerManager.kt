package io.github.dockyardmc.player

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.World
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

object PlayerManager {

    var entityCounter = AtomicInteger()
    val players: MutableList<Player> = mutableListOf()
    val playerToProcessorMap = mutableMapOf<UUID, PacketProcessor>()

    fun add(player: Player, processor: PacketProcessor) {
        players.add(player)
        playerToProcessorMap[player.uuid] = processor
        processor.player = player
        Events.dispatch(PlayerConnectEvent(player))
    }

    fun remove(player: Player) {
        players.remove(player)
        playerToProcessorMap.remove(player.uuid)
    }

    fun sendToEveryoneInWorld(world: World, packet: ClientboundPacket) {
        val filtered = players.filter { it.world != null && it.world == world }
        filtered.forEach { it.connection.sendPacket(packet) }
    }
}