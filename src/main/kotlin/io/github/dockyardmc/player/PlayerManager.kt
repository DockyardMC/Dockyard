package io.github.dockyardmc.player

import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.ktor.util.network.*
import io.netty.channel.ChannelHandlerContext
import java.util.*

object PlayerManager {

    private val innerPlayers: MutableList<Player> = mutableListOf()
    private val innerUUIDToPlayerMap: MutableMap<UUID, Player> = mutableMapOf()
    private val innerUsernameToPlayerMap: MutableMap<String, Player> = mutableMapOf()
    private val innerPlayerToEntityIdMap = mutableMapOf<Int, Player>()

    val players get() = innerPlayers.toList()
    val uuidToPlayerMap get() = innerUUIDToPlayerMap.toMap()
    val playerToEntityIdMap get() = innerPlayerToEntityIdMap.toMap()
    val usernameToPlayerMap get() = innerUsernameToPlayerMap.toMap()

    fun getPlayerByUsernameOrNull(username: String): Player? {
        return usernameToPlayerMap[username]
    }

    fun getPlayerByUsername(username: String): Player {
        return usernameToPlayerMap[username] ?: throw IllegalArgumentException("Player with username $username is not online on the server!")
    }

    fun getPlayerByUUIDOrNull(uuid: String): Player? {
        return getPlayerByUUIDOrNull(UUID.fromString(uuid))
    }

    fun getPlayerByUUIDOrNull(uuid: UUID): Player? {
        return uuidToPlayerMap[uuid]
    }

    fun getPlayerByUUID(uuid: String): Player {
        return getPlayerByUUID(UUID.fromString(uuid))
    }

    fun getPlayerByUUID(uuid: UUID): Player {
        return getPlayerByUUIDOrNull(uuid) ?: throw IllegalArgumentException("Player with uuid $uuid is not online on the server")
    }

    fun add(player: Player, processor: PlayerNetworkManager) {
        synchronized(innerPlayers) {
            innerPlayers.add(player)
            innerUsernameToPlayerMap[player.username] = player
        }
        synchronized(innerPlayerToEntityIdMap) {
            innerPlayerToEntityIdMap[player.id] = player
        }

        processor.player = player
        EntityManager.addPlayer(player)
        Events.dispatch(PlayerConnectEvent(player))
    }

    fun remove(player: Player) {
        player.isConnected = false
        //TODO send remove player info packets
        player.viewers.toList().forEach {
            player.removeViewer(it);
            it.removeViewer(player)
        }

        synchronized(innerPlayers) {
            innerPlayers.remove(player)
            innerUsernameToPlayerMap.remove(player.username)
        }
        synchronized(playerToEntityIdMap) {
            innerPlayerToEntityIdMap.remove(player.id)
        }
        player.world.removePlayer(player)
        player.world.removeEntity(player)
        player.dispose()
    }

    fun sendToEveryoneInWorld(world: World, packet: ClientboundPacket) {
        val filtered = players.filter { it.world == world }
        filtered.forEach { it.sendPacket(packet) }
    }

    fun createNewPlayer(username: String, uuid: UUID, connection: ChannelHandlerContext, networkManager: PlayerNetworkManager): Player {
        val player = Player(
            username = username,
            id = EntityManager.entityIdCounter.incrementAndGet(),
            uuid = uuid,
            world = WorldManager.mainWorld,
            address = connection.channel().remoteAddress().address,
            connection = connection,
            networkManager = networkManager
        )
        this.add(player, networkManager)
        return player
    }
}