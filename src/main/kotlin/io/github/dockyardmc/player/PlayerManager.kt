package io.github.dockyardmc.player

import io.github.dockyardmc.entities.EntityManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.World

object PlayerManager {

    val players: MutableList<Player> = mutableListOf()
    val playerToEntityIdMap = mutableMapOf<Int, Player>()

    init {
        Events.on<ServerTickEvent> {
            players.toList().forEach(Player::tick)
        }
    }

    fun add(player: Player, processor: PlayerNetworkManager) {
        players.add(player)
        playerToEntityIdMap[player.entityId] = player
        processor.player = player
        Events.dispatch(PlayerConnectEvent(player))
    }

    fun remove(player: Player) {
        player.isConnected = false
        player.viewers.toMutableList().forEach { player.removeViewer(it, true); it.removeViewer(player, true) }

        players.remove(player)
        playerToEntityIdMap.remove(player.entityId)
        EntityManager.entities.remove(player)
        player.world.players.removeIfPresent(player)
        player.world.entities.removeIfPresent(player)
        player.dispose()
    }

    fun sendToEveryoneInWorld(world: World, packet: ClientboundPacket) {
        val filtered = players.filter { it.world == world }
        filtered.forEach { it.sendPacket(packet) }
    }
}