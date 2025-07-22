package io.github.dockyardmc.noxesium.protocol

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PluginMessageReceivedEvent
import io.github.dockyardmc.noxesium.Noxesium
import io.github.dockyardmc.noxesium.protocol.serverbound.NoxesiumServerboundHandlers
import io.github.dockyardmc.noxesium.protocol.serverbound.ServerboundNoxesiumClientInformationPacket
import io.github.dockyardmc.noxesium.protocol.serverbound.ServerboundNoxesiumClientSettingsPacket
import io.github.dockyardmc.noxesium.protocol.serverbound.ServerboundNoxesiumQibTriggeredPacket
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.utils.MutableBiMap
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException

object NoxesiumProtocol {
    val packets: MutableBiMap<String, NoxesiumPacketInfo<out NoxesiumPacket>> = MutableBiMap()

    @Suppress("UNCHECKED_CAST")
    fun register() {
        Events.on<PluginMessageReceivedEvent> { event ->
            val split = event.channel.split(":")

            val namespace = split.getOrNull(0) ?: throw DecoderException("Plugin message does not have valid identifier")
            val channel = split.getOrNull(1) ?: throw DecoderException("Plugin message does not have valid identifier")

            if (event.channel == Noxesium.PACKET_NAMESPACE) {
                val packetInfo = packets.getByKeyOrNull(channel) ?: throw DecoderException("No noxesium packet for $channel ($namespace)")
                handlePacket<NoxesiumPacket>(packetInfo as NoxesiumPacketInfo<NoxesiumPacket>, event.player, event.data)
                event.cancel()
            }
        }
    }

    private fun <T : NoxesiumPacket> handlePacket(packetInfo: NoxesiumPacketInfo<T>, player: Player, buffer: ByteBuf) {
        val packet = packetInfo.streamCodec.readNetwork(buffer)
        packetInfo.handler?.invoke(player, packet)
    }

    init {
        packets.put("client_info", NoxesiumPacketInfo(ServerboundNoxesiumClientInformationPacket.STREAM_CODEC, NoxesiumServerboundHandlers::handleClientInfo))
        packets.put("client_settings", NoxesiumPacketInfo(ServerboundNoxesiumClientSettingsPacket.STREAM_CODEC, NoxesiumServerboundHandlers::handleClientSettings))
        packets.put("qib_triggered", NoxesiumPacketInfo(ServerboundNoxesiumQibTriggeredPacket.STREAM_CODEC, NoxesiumServerboundHandlers::handleQibTriggered))
    }

    data class NoxesiumPacketInfo<T : NoxesiumPacket>(val streamCodec: Codec<T>, val handler: ((Player, T) -> Unit)? = null)
}