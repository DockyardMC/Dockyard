package io.github.dockyardmc.noxesium.protocol

import io.github.dockyardmc.noxesium.Noxesium
import io.github.dockyardmc.protocol.packets.configurations.ClientboundPlayPluginMessagePacket
import io.github.dockyardmc.tide.stream.StreamCodec

interface NoxesiumPacket {
    fun getStreamCodec(): StreamCodec<out NoxesiumPacket>

    @Suppress("UNCHECKED_CAST")
    fun getPluginMessagePacket(): ClientboundPlayPluginMessagePacket {
        val packetInfo = Noxesium.clientboundPackets.getByKey(this::class) as Noxesium.NoxesiumClientboundPacketInfo<NoxesiumPacket>
        return packetInfo.getPluginMessagePacket(this)
    }
}