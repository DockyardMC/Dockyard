package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSuggestionsResponse(transactionId: Int, start: Int, length: Int, suggestions: List<String>) : ClientboundPacket() {

    init {
        data.writeVarInt(transactionId)
        data.writeVarInt(start)
        data.writeVarInt(length)
        data.writeVarInt(suggestions.size)
        suggestions.forEach {
            data.writeString(it)
            data.writeBoolean(false)
        }
    }

}