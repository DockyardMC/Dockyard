package io.github.dockyardmc.player

import io.github.dockyardmc.entities.EntityManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.World
import java.util.UUID

object PlayerManager {

    val players: MutableList<Player> = mutableListOf()
    val playerToProcessorMap = mutableMapOf<UUID, PlayerNetworkManager>()
    val playerToEntityIdMap = mutableMapOf<Int, Player>()

    fun Player.getProcessor(): PlayerNetworkManager = playerToProcessorMap[this.uuid]!!

    init {
        Events.on<ServerTickEvent> {
            players.forEach(Player::tick)
        }
    }

    fun add(player: Player, processor: PlayerNetworkManager) {
        players.add(player)
        playerToProcessorMap[player.uuid] = processor
        playerToEntityIdMap[player.entityId] = player
        processor.player = player
        Events.dispatch(PlayerConnectEvent(player))
    }

    fun remove(player: Player) {
        player.isConnected = false
        player.viewers.toMutableList().forEach { player.removeViewer(it, true); it.removeViewer(player, true) }

        players.remove(player)
        playerToProcessorMap.remove(player.uuid)
        playerToEntityIdMap.remove(player.entityId)
        EntityManager.entities.remove(player)
        player.world.players.removeIfPresent(player)
        player.world.entities.removeIfPresent(player)
    }

    fun sendToEveryoneInWorld(world: World, packet: ClientboundPacket) {
        val filtered = players.filter { it.world == world }
        filtered.forEach { it.sendPacket(packet) }
    }
}