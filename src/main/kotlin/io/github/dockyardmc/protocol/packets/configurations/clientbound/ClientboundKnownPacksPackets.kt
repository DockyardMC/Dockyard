package io.github.dockyardmc.protocol.packets.configurations.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundKnownPacksPackets(knowPackets: MutableList<KnownPack>): ClientboundPacket() {

    init {
        buffer.writeVarInt(knowPackets.size)
        knowPackets.forEach {
            buffer.writeString(it.namespace)
            buffer.writeString(it.id)
            buffer.writeString(it.version)
        }
    }
}

data class KnownPack(
    val namespace: String,
    val id: String,
    val version: String
)