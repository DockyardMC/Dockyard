package io.github.dockyardmc.noxesium

import com.noxcrew.noxesium.api.NoxesiumReferences
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDisconnectEvent
import io.github.dockyardmc.events.noxesium.NoxesiumClientInformationEvent
import io.github.dockyardmc.events.noxesium.NoxesiumClientSettingsEvent
import io.github.dockyardmc.noxesium.protocol.clientbound.*
import io.github.dockyardmc.noxesium.protocol.serverbound.*
import io.github.dockyardmc.noxesium.rules.NoxesiumEntityRuleContainer
import io.github.dockyardmc.noxesium.rules.NoxesiumRuleContainer
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.profiler.profiler
import io.github.dockyardmc.protocol.plugin.PluginMessageRegistry

object Noxesium {

    private val packetHandler: NoxesiumServerboundHandlers = NoxesiumServerboundHandlers()
    private val eventPool = EventPool(Events, "Noxesium Listeners")

    const val IMMOVABLE_TAG = "noxesium:immovable"
    const val PACKET_NAMESPACE = "${NoxesiumReferences.NAMESPACE}-v2"

    private val _players: MutableList<Player> = mutableListOf()
    val players: List<Player> get() = _players.toList()

    val globalRuleContainer: NoxesiumRuleContainer = NoxesiumRuleContainer()
    val globalEntityRuleContainer: NoxesiumEntityRuleContainer = NoxesiumEntityRuleContainer()

    private val waiting: MutableList<Player> = mutableListOf()

    fun addPlayer(player: Player) {
        waiting.add(player)
        player.sendPluginMessage(ClientboundNoxesiumServerInformationPacket(NoxesiumReferences.VERSION))
    }

    fun removePlayer(player: Player) {
        _players.remove(player)
        waiting.remove(player)
        globalRuleContainer.removeViewer(player)
    }

    fun register() {
        profiler("Load Noxesium integration") {

            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:change_server_rules", ClientboundNoxesiumChangeServerRulesPacket::class, ClientboundNoxesiumChangeServerRulesPacket.STREAM_CODEC)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:reset_server_rules", ClientboundNoxesiumResetServerRulesPacket::class, ClientboundNoxesiumResetServerRulesPacket.STREAM_CODEC)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:reset", ClientboundNoxesiumResetPacket::class, ClientboundNoxesiumResetPacket.STREAM_CODEC)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:server_info", ClientboundNoxesiumServerInformationPacket::class, ClientboundNoxesiumServerInformationPacket.STREAM_CODEC)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:start_sound", ClientboundNoxesiumCustomSoundStartPacket::class, ClientboundNoxesiumCustomSoundStartPacket.STREAM_CODEC)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:modify_sound", ClientboundNoxesiumCustomSoundModifyPacket::class, ClientboundNoxesiumCustomSoundModifyPacket.STREAM_CODEC)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:stop_sound", ClientboundNoxesiumCustomSoundStopPacket::class, ClientboundNoxesiumCustomSoundStopPacket.STREAM_CODEC)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:change_extra_entity_data", ClientboundNoxesiumSetExtraEntityDataPacket::class, ClientboundNoxesiumSetExtraEntityDataPacket.STREAM_CODEC)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:reset_extra_entity_data", ClientboundNoxesiumResetExtraEntityDataPacket::class, ClientboundNoxesiumResetExtraEntityDataPacket.STREAM_CODEC)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:open_link", ClientboundNoxesiumOpenLinkPacket::class, ClientboundNoxesiumOpenLinkPacket.STREAM_CODEC)

            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:client_info", ServerboundNoxesiumClientInformationPacket::class, ServerboundNoxesiumClientInformationPacket.STREAM_CODEC, packetHandler::handleClientInfo)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:client_settings", ServerboundNoxesiumClientSettingsPacket::class, ServerboundNoxesiumClientSettingsPacket.STREAM_CODEC, packetHandler::handleClientSettings)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:qib_triggered", ServerboundNoxesiumQibTriggeredPacket::class, ServerboundNoxesiumQibTriggeredPacket.STREAM_CODEC, packetHandler::handleQibTriggered)
            PluginMessageRegistry.registerPlay("$PACKET_NAMESPACE:riptide", ServerboundNoxesiumRiptidePacket::class, ServerboundNoxesiumRiptidePacket.STREAM_CODEC, packetHandler::handleRiptide)

            eventPool.on<NoxesiumClientInformationEvent> { event ->
                if (!waiting.contains(event.player)) return@on
                if (event.protocolVersion != NoxesiumReferences.VERSION) {
                    val state = if (event.protocolVersion > NoxesiumReferences.VERSION) "more up-to-date" else "outdated"
                    log("${event.player} is using $state of noxesium than the server is running (player: ${event.protocolVersion}, server: ${NoxesiumReferences.VERSION})", LogType.WARNING)
                    waiting.remove(event.player)
                } else {
                    waiting.remove(event.player)
                    _players.add(event.player)
                    globalRuleContainer.addViewer(event.player)
                    event.player.noxesiumIntegration.isUsingNoxesium.value = true
                }
            }

            eventPool.on<NoxesiumClientSettingsEvent> { event ->
                if (!_players.contains(event.player)) return@on
                event.player.noxesiumIntegration.settings = event.clientSettings
            }

            eventPool.on<PlayerDisconnectEvent> { event ->
                val player = event.player
                this.globalEntityRuleContainer.removeEntity(player)
                this.globalEntityRuleContainer.removeViewer(player)
                this.globalRuleContainer.removeViewer(player)

                PlayerManager.players.forEach { loopPlayer ->
                    loopPlayer.noxesiumIntegration.entityRulesContainer.removeEntity(event.player)
                }
            }
        }
    }
}