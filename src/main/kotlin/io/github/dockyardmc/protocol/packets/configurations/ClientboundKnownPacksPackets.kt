package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Clientbound Known Packs")
class ClientboundKnownPacksPackets(knowPackets: MutableList<KnownPack>): ClientboundPacket(0x0E, ProtocolState.CONFIGURATION) {

    init {
        data.writeVarInt(knowPackets.size)
        knowPackets.forEach {
            data.writeUtf(it.namespace)
            data.writeUtf(it.id)
            data.writeUtf(it.version)
        }
    }

}

data class KnownPack(
    val namespace: String,
    val id: String,
    val version: String
)