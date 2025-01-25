package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Health")
@ClientboundPacketInfo(0x5D, ProtocolState.PLAY)
class ClientboundSetHealthPacket(var health: Float, var food: Int, var saturation: Float): ClientboundPacket() {

    init {
        data.writeFloat(health)
        data.writeVarInt(food)
        data.writeFloat(saturation)
    }
}