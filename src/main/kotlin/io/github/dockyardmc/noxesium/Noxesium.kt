package io.github.dockyardmc.noxesium

import com.noxcrew.noxesium.api.NoxesiumReferences
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PluginMessageReceivedEvent
import io.github.dockyardmc.events.noxesium.NoxesiumPacketReceiveEvent
import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.noxesium.protocol.clientbound.*
import io.github.dockyardmc.noxesium.protocol.serverbound.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.profiler.profiler
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.utils.MutableBiMap
import io.github.dockyardmc.utils.getPlayerEventContext
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import kotlin.reflect.KClass

object Noxesium {
    val serverboundPackets: MutableBiMap<String, NoxesiumServerboundPacketInfo<out NoxesiumPacket>> = MutableBiMap()
    val clientboundPackets: MutableBiMap<KClass<out NoxesiumPacket>, NoxesiumClientboundPacketInfo<out NoxesiumPacket>> = MutableBiMap()

    const val IMMOVABLE_TAG = "noxesium:immovable"
    const val BUKKIT_TAG = "PublicBukkitValues"
    const val PACKET_NAMESPACE = "${NoxesiumReferences.NAMESPACE}-v2"

    val BUKKIT_COMPOUND = nbt {
        withBoolean(IMMOVABLE_TAG, true)
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

            Events.on<PluginMessageReceivedEvent> { event ->
                val split = event.channel.split(":")

                val namespace = split.getOrNull(0) ?: throw DecoderException("Plugin message does not have valid identifier")
                val channel = split.getOrNull(1) ?: throw DecoderException("Plugin message does not have valid identifier")

                if (event.channel == PACKET_NAMESPACE) {
                    val packetInfo = serverboundPackets.getByKeyOrNull(channel) ?: throw DecoderException("No noxesium packet for $channel ($namespace)")
                    handlePacket<NoxesiumPacket>(packetInfo as NoxesiumServerboundPacketInfo<NoxesiumPacket>, event.player, event.data)
                    event.cancel()
                }
            }
        }
    }

    private fun <T : NoxesiumPacket> handlePacket(packetInfo: NoxesiumServerboundPacketInfo<T>, player: Player, buffer: ByteBuf) {
        val packet = packetInfo.streamCodec.readNetwork(buffer)

        val event = NoxesiumPacketReceiveEvent(player, packet, getPlayerEventContext(player))
        Events.dispatch(event)
        if (event.cancelled) return

        packetInfo.handler?.invoke(player, packet)
    }

    data class NoxesiumServerboundPacketInfo<T : NoxesiumPacket>(val streamCodec: Codec<T>, val handler: ((Player, T) -> Unit)? = null)
    data class NoxesiumClientboundPacketInfo<T : NoxesiumPacket>(val identifier: String, val streamCodec: Codec<T>)
}