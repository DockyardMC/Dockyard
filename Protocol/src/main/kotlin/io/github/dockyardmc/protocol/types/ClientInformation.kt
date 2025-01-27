package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf

data class ClientInformation(
    var locale: String,
    var viewDistance: Int,
    var chatMode: ChatMode,
    var chatColors: Boolean,
    var displayedSkinParts: Byte,
    var mainHandSide: PlayerHand,
    var enableTextFiltering: Boolean,
    var allowServerListing: Boolean,
    var particleSettings: ParticleSettings
): NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(locale)
        buffer.writeByte(viewDistance)
        buffer.writeEnum<ChatMode>(chatMode)
        buffer.writeBoolean(chatColors)
        buffer.writeByte(displayedSkinParts)
        buffer.writeEnum(mainHandSide)
        buffer.writeBoolean(enableTextFiltering)
        buffer.writeBoolean(allowServerListing)
        buffer.writeEnum(particleSettings)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientInformation {
            return ClientInformation(
                buffer.readString(),
                buffer.readByte().toInt(),
                buffer.readEnum(),
                buffer.readBoolean(),
                buffer.readByte(),
                buffer.readEnum(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readEnum(),
            )
        }
    }

    enum class PlayerHand {
        MAIN_HAND,
        OFF_HAND
    }

    enum class ParticleSettings {
        ALL,
        DECREASED,
        MINIMAL
    }

    enum class ChatMode {
        FULL,
        SYSTEM,
        NONE
    }
}