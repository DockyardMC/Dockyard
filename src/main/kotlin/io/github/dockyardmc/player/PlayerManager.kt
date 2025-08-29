package io.github.dockyardmc.player

import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerInfoRemovePacket
import io.github.dockyardmc.provider.PlayerMessageProvider
import io.github.dockyardmc.provider.PlayerPacketProvider
import io.github.dockyardmc.utils.getPlayerEventContext
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager
import io.ktor.util.network.*
import io.netty.channel.ChannelHandlerContext
import java.util.*

object PlayerManager : PlayerMessageProvider, PlayerPacketProvider {

    override val playerGetter: Collection<Player>
        get() = _players

    private val _players: MutableList<Player> = mutableListOf()
    private val _UUIDToPlayerMap: MutableMap<UUID, Player> = mutableMapOf()
    private val _UsernameToPlayerMap: MutableMap<String, Player> = mutableMapOf()
    private val _PlayerToEntityIdMap = mutableMapOf<Int, Player>()

    val players get() = _players.toList()
    val uuidToPlayerMap get() = _UUIDToPlayerMap.toMap()
    val playerToEntityIdMap get() = _PlayerToEntityIdMap.toMap()
    val usernameToPlayerMap get() = _UsernameToPlayerMap.toMap()

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
        synchronized(_players) {
            _players.add(player)
            _UsernameToPlayerMap[player.username] = player
        }
        synchronized(_PlayerToEntityIdMap) {
            _PlayerToEntityIdMap[player.id] = player
        }
        synchronized(_UUIDToPlayerMap) {
            _UUIDToPlayerMap[player.uuid] = player
        }

        processor.player = player
        EntityManager.addPlayer(player)
        Events.dispatch(PlayerConnectEvent(player, getPlayerEventContext(player)))
    }

    fun remove(player: Player) {
        player.isConnected = false
        EntityManager.removePlayer(player)
        SkinManager.uuidToSkinCache.remove(player.uuid)
        SkinManager.usernameToUuidCache.remove(player.username)

        player.viewers.toList().forEach { viewer ->
            viewer.sendPacket(ClientboundPlayerInfoRemovePacket(player))
            player.removeViewer(viewer)
            viewer.removeViewer(player)
        }

        synchronized(_players) {
            _players.remove(player)
            _UsernameToPlayerMap.remove(player.username)
        }
        synchronized(playerToEntityIdMap) {
            _PlayerToEntityIdMap.remove(player.id)
        }
        synchronized(_UUIDToPlayerMap) {
            _UUIDToPlayerMap.remove(player.uuid)
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