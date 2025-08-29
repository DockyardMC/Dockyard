package io.github.dockyardmc.noxesium

import com.noxcrew.noxesium.api.NoxesiumReferences
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.events.*
import io.github.dockyardmc.events.noxesium.NoxesiumClientInformationEvent
import io.github.dockyardmc.events.noxesium.NoxesiumClientSettingsEvent
import io.github.dockyardmc.events.noxesium.NoxesiumPacketReceiveEvent
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.noxesium.protocol.clientbound.*
import io.github.dockyardmc.noxesium.protocol.serverbound.*
import io.github.dockyardmc.noxesium.rules.NoxesiumEntityRuleContainer
import io.github.dockyardmc.noxesium.rules.NoxesiumRuleContainer
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.profiler.profiler
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayPluginMessagePacket
import io.github.dockyardmc.protocol.plugin.PluginMessages
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.utils.MutableBiMap
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.DecoderException
import kotlin.reflect.KClass

object Noxesium {
    val serverboundPackets: MutableBiMap<String, NoxesiumServerboundPacketInfo<out NoxesiumPacket>> = MutableBiMap()
    val clientboundPackets: MutableBiMap<KClass<out NoxesiumPacket>, NoxesiumClientboundPacketInfo<out NoxesiumPacket>> = MutableBiMap()

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
        player.sendPacket(ClientboundNoxesiumServerInformationPacket(NoxesiumReferences.VERSION).getPluginMessagePacket())
    }

    fun removePlayer(player: Player) {
        _players.remove(player)
        waiting.remove(player)
        globalRuleContainer.removeViewer(player)
    }

    @Suppress("UNCHECKED_CAST")
    fun register() {
        profiler("Load Noxesium integration") {

            clientboundPackets.put(ClientboundNoxesiumChangeServerRulesPacket::class, NoxesiumClientboundPacketInfo("change_server_rules", ClientboundNoxesiumChangeServerRulesPacket.STREAM_CODEC))
            clientboundPackets.put(ClientboundNoxesiumResetServerRulesPacket::class, NoxesiumClientboundPacketInfo("reset_server_rules", ClientboundNoxesiumResetServerRulesPacket.STREAM_CODEC))
            clientboundPackets.put(ClientboundNoxesiumResetPacket::class, NoxesiumClientboundPacketInfo("reset", ClientboundNoxesiumResetPacket.STREAM_CODEC))
            clientboundPackets.put(ClientboundNoxesiumServerInformationPacket::class, NoxesiumClientboundPacketInfo("server_info", ClientboundNoxesiumServerInformationPacket.STREAM_CODEC))
            clientboundPackets.put(ClientboundNoxesiumCustomSoundStartPacket::class, NoxesiumClientboundPacketInfo("start_sound", ClientboundNoxesiumCustomSoundStartPacket.STREAM_CODEC))
            clientboundPackets.put(ClientboundNoxesiumCustomSoundModifyPacket::class, NoxesiumClientboundPacketInfo("modify_sound", ClientboundNoxesiumCustomSoundModifyPacket.STREAM_CODEC))
            clientboundPackets.put(ClientboundNoxesiumCustomSoundStopPacket::class, NoxesiumClientboundPacketInfo("stop_sound", ClientboundNoxesiumCustomSoundStopPacket.STREAM_CODEC))
            clientboundPackets.put(ClientboundNoxesiumSetExtraEntityDataPacket::class, NoxesiumClientboundPacketInfo("change_extra_entity_data", ClientboundNoxesiumSetExtraEntityDataPacket.STREAM_CODEC))
            clientboundPackets.put(ClientboundNoxesiumResetExtraEntityDataPacket::class, NoxesiumClientboundPacketInfo("reset_extra_entity_data", ClientboundNoxesiumResetExtraEntityDataPacket.STREAM_CODEC))
            clientboundPackets.put(ClientboundNoxesiumOpenLinkPacket::class, NoxesiumClientboundPacketInfo("open_link", ClientboundNoxesiumOpenLinkPacket.STREAM_CODEC))

            serverboundPackets.put("client_info", NoxesiumServerboundPacketInfo(ServerboundNoxesiumClientInformationPacket.STREAM_CODEC, NoxesiumServerboundHandlers::handleClientInfo))
            serverboundPackets.put("client_settings", NoxesiumServerboundPacketInfo(ServerboundNoxesiumClientSettingsPacket.STREAM_CODEC, NoxesiumServerboundHandlers::handleClientSettings))
            serverboundPackets.put("qib_triggered", NoxesiumServerboundPacketInfo(ServerboundNoxesiumQibTriggeredPacket.STREAM_CODEC, NoxesiumServerboundHandlers::handleQibTriggered))
            serverboundPackets.put("riptide", NoxesiumServerboundPacketInfo(ServerboundNoxesiumRiptidePacket.STREAM_CODEC, NoxesiumServerboundHandlers::handleRiptide))

            PluginMessages.registeredChannels.addAll(clientboundPackets.valueToKey().map { "${PACKET_NAMESPACE}:${it.key.identifier}" })
            PluginMessages.registeredChannels.addAll(serverboundPackets.keyToValue().map { "${PACKET_NAMESPACE}:${it.key}" })

            eventPool.on<PluginMessageReceivedEvent> { event ->
                val split = event.channel.split(":")

                val namespace = split.getOrNull(0) ?: throw DecoderException("Plugin message does not have valid identifier")
                val channel = split.getOrNull(1) ?: throw DecoderException("Plugin message does not have valid identifier")

                if (namespace == PACKET_NAMESPACE) {
                    val packetInfo = serverboundPackets.getByKeyOrNull(channel) ?: throw DecoderException("No noxesium packet for $channel ($namespace)")
                    handlePacket<NoxesiumPacket>(packetInfo as NoxesiumServerboundPacketInfo<NoxesiumPacket>, event.player, event.data)
                    event.cancel()
                }
            }

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

            eventPool.on<RegisterPluginChannelsEvent> { event ->
                if (event.player.networkManager.state != ProtocolState.PLAY) return@on
                PluginMessages.sendRegisteredChannels(event.player)

                if (event.channels.contains("$PACKET_NAMESPACE:${clientboundPackets.getByKey(ClientboundNoxesiumServerInformationPacket::class).identifier}")) {
                    addPlayer(event.player)
                }
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

    private fun <T : NoxesiumPacket> handlePacket(packetInfo: NoxesiumServerboundPacketInfo<T>, player: Player, buffer: ByteBuf) {
        val packet = packetInfo.streamCodec.read(buffer)

        val event = NoxesiumPacketReceiveEvent(player, packet, getPlayerEventContext(player))
        Events.dispatch(event)
        if (event.cancelled) return

        packetInfo.handler?.invoke(player, packet)
    }

    data class NoxesiumServerboundPacketInfo<T : NoxesiumPacket>(val streamCodec: StreamCodec<T>, val handler: ((Player, T) -> Unit)? = null)

    data class NoxesiumClientboundPacketInfo<T : NoxesiumPacket>(val identifier: String, val streamCodec: StreamCodec<T>) {
        fun getPluginMessagePacket(value: T): ClientboundPlayPluginMessagePacket {
            val buffer = Unpooled.buffer()
            streamCodec.write(buffer, value)
            return ClientboundPlayPluginMessagePacket("$PACKET_NAMESPACE:$identifier", buffer)
        }
    }
}