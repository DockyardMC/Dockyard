package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSuggestionsResponse(transactionId: Int, start: Int, length: Int, suggestions: List<String>) : ClientboundPacket() {

    init {
        buffer.writeVarInt(transactionId)
        buffer.writeVarInt(start)
        buffer.writeVarInt(length)
        buffer.writeVarInt(suggestions.size)
        suggestions.forEach {
            buffer.writeString(it)
            buffer.writeBoolean(false)
        }
    }

}