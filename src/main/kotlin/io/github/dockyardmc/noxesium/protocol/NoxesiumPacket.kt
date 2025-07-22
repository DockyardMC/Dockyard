package io.github.dockyardmc.noxesium.protocol

import io.github.dockyardmc.noxesium.Noxesium
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayPluginMessagePacket
import io.github.dockyardmc.tide.Codec

interface NoxesiumPacket {
    fun getStreamCodec(): Codec<out NoxesiumPacket>

    @Suppress("UNCHECKED_CAST")
    fun getPluginMessagePacket(): ClientboundPlayPluginMessagePacket {
        val packetInfo = Noxesium.clientboundPackets.getByKey(this::class) as Noxesium.NoxesiumClientboundPacketInfo<NoxesiumPacket>
        return packetInfo.getPluginMessagePacket(this)
    }
}