package io.github.dockyardmc.resourcepack

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.configurations.clientbound.ClientboundConfigurationAddResourcePackPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayAddResourcepackPacket
import io.github.dockyardmc.utils.debug
import java.util.*
import java.util.concurrent.CompletableFuture

object ResourcepackManager {

    private val _queuedConfigurationPacks: MutableMap<Player, MutableList<PendingResourcePack>> = mutableMapOf()
    private val _pendingResourcePacks: MutableMap<Player, MutableMap<UUID, PendingResourcePack>> = mutableMapOf()
    val pendingResourcePacks get() = _pendingResourcePacks.toMap()

    fun remove(player: Player) {
        _pendingResourcePacks.remove(player)
        _queuedConfigurationPacks.remove(player)
    }

    fun handleResponse(player: Player, uuid: UUID, response: ResourcePack.Status) {
        val pendingPacks = pendingResourcePacks[player] ?: return
        val pack = pendingPacks[uuid] ?: return

        when (response) {
            ResourcePack.Status.FAILED_TO_RELOAD,
            ResourcePack.Status.INVALID_URL,
            ResourcePack.Status.DECLINED,
            ResourcePack.Status.DISCARDED,
            ResourcePack.Status.FAILED_TO_DOWNLOAD,
            ResourcePack.Status.SUCCESSFULLY_LOADED -> {
                pack.future.complete(response)
                pendingPacks.remove(uuid)
                if (response == ResourcePack.Status.SUCCESSFULLY_LOADED) {
                    player.resourcepacks.add(pack.resourcePack)
                }
            }

            else -> {}
        }
    }

    data class PendingResourcePack(
        val player: Player,
        val resourcePack: ResourcePack,
        val protocolState: ProtocolState,
        val future: CompletableFuture<ResourcePack.Status>
    )

    fun sendQueuedConfigurationPacks(player: Player): CompletableFuture<Void> {
        val packs = _queuedConfigurationPacks[player]
        val futures = mutableListOf<CompletableFuture<ResourcePack.Status>>()
        packs?.forEach { pack ->
            futures.add(pack.future)
            player.sendPacket(ClientboundConfigurationAddResourcePackPacket(pack.resourcePack))
        }

        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    private fun queueConfigurationResourcePack(player: Player, resourcePack: PendingResourcePack) {
        val existing = _queuedConfigurationPacks[player] ?: mutableListOf()
        existing.add(resourcePack)
        _queuedConfigurationPacks[player] = existing
        resourcePack.future.thenAccept {
            _queuedConfigurationPacks.remove(player)
        }
    }

    fun sendResourcePack(player: Player, resourcePack: ResourcePack): CompletableFuture<ResourcePack.Status> {

        val future = CompletableFuture<ResourcePack.Status>()
        val pendingPack = PendingResourcePack(player, resourcePack, player.networkManager.state, future)
        val pendingPacks = _pendingResourcePacks[player] ?: mutableMapOf()
        pendingPacks[resourcePack.uuid] = pendingPack
        _pendingResourcePacks[player] = pendingPacks

        if (player.networkManager.state == ProtocolState.CONFIGURATION) {
            queueConfigurationResourcePack(player, pendingPack)
            debug("Queued resource pack for $player")
        } else {
            val packet = if (player.networkManager.state == ProtocolState.CONFIGURATION) ClientboundConfigurationAddResourcePackPacket(resourcePack) else ClientboundPlayAddResourcepackPacket(resourcePack)
            player.sendPacket(packet)
        }

        return future
    }
}