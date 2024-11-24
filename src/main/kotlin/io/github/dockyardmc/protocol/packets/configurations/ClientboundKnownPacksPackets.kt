package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Clientbound Known Packs")
@ClientboundPacketInfo(0x0E, ProtocolState.CONFIGURATION)
class ClientboundKnownPacksPackets(knowPackets: MutableList<KnownPack>): ClientboundPacket() {

    init {
        data.writeVarInt(knowPackets.size)
        knowPackets.forEach {
            data.writeString(it.namespace)
            data.writeString(it.id)
            data.writeString(it.version)
        }
    }
}

data class KnownPack(
    val namespace: String,
    val id: String,
    val version: String
)