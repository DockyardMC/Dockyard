package io.github.dockyardmc.player

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.ChatSession
import io.github.dockyardmc.protocol.types.GameProfile
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

interface PlayerInfoUpdate : NetworkWritable {

    val type: Type

    enum class Type(val mask: Int) {
        ADD_PLAYER(0x01),
        INITIALIZE_CHAT(0x02),
        UPDATE_GAME_MODE(0x04),
        UPDATE_LISTED(0x08),
        UPDATE_LATENCY(0x10),
        UPDATE_DISPLAY_NAME(0x20),
        UPDATE_LIST_ORDER(0x40),
        UPDATE_HAT(0x80);
    }

    data class UpdateHat(val visible: Boolean) : PlayerInfoUpdate {
        override val type: Type = Type.UPDATE_HAT

        override fun write(buffer: ByteBuf) {
            buffer.writeBoolean(visible)
        }

        companion object : NetworkReadable<UpdateHat> {
            override fun read(buffer: ByteBuf): UpdateHat {
                return UpdateHat(buffer.readBoolean())
            }
        }
    }

    data class UpdateListOrder(val order: Int) : PlayerInfoUpdate {
        override val type: Type = Type.UPDATE_LIST_ORDER

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(order)
        }

        companion object : NetworkReadable<UpdateListOrder> {
            override fun read(buffer: ByteBuf): UpdateListOrder {
                return UpdateListOrder(buffer.readVarInt())
            }
        }
    }

    data class UpdateDisplayName(val displayName: Component?) : PlayerInfoUpdate {
        constructor(displayName: String?) : this(displayName?.toComponent())

        override val type: Type = Type.UPDATE_DISPLAY_NAME

        override fun write(buffer: ByteBuf) {
            buffer.writeOptional(displayName, ByteBuf::writeTextComponent)
        }

        companion object : NetworkReadable<UpdateDisplayName> {
            override fun read(buffer: ByteBuf): UpdateDisplayName {
                return UpdateDisplayName(buffer.readOptional(ByteBuf::readTextComponent))
            }
        }
    }

    data class UpdateLatency(val latency: Int) : PlayerInfoUpdate {
        override val type: Type = Type.UPDATE_LATENCY

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(latency)
        }

        companion object : NetworkReadable<UpdateLatency> {
            override fun read(buffer: ByteBuf): UpdateLatency {
                return UpdateLatency(buffer.readVarInt())
            }
        }
    }

    data class UpdateListed(val listed: Boolean) : PlayerInfoUpdate {
        override val type: Type = Type.UPDATE_LISTED

        override fun write(buffer: ByteBuf) {
            buffer.writeBoolean(listed)
        }

        companion object : NetworkReadable<UpdateListed> {
            override fun read(buffer: ByteBuf): UpdateListed {
                return UpdateListed(buffer.readBoolean())
            }
        }
    }

    data class UpdateGameMode(val gameMode: GameMode) : PlayerInfoUpdate {
        override val type: Type = Type.UPDATE_GAME_MODE

        override fun write(buffer: ByteBuf) {
            buffer.writeEnum(gameMode)
        }

        companion object : NetworkReadable<UpdateGameMode> {
            override fun read(buffer: ByteBuf): UpdateGameMode {
                return UpdateGameMode(buffer.readEnum())
            }
        }
    }

    data class InitializeChat(val chatSession: ChatSession?) : PlayerInfoUpdate {
        override val type: Type = Type.INITIALIZE_CHAT

        override fun write(buffer: ByteBuf) {
            buffer.writeOptional(chatSession, ChatSession::write)
        }

        companion object : NetworkReadable<InitializeChat> {
            override fun read(buffer: ByteBuf): InitializeChat {
                return InitializeChat(buffer.readOptional(ChatSession::read))
            }
        }
    }

    data class AddPlayer(val name: String, val properties: List<GameProfile.Property>) : PlayerInfoUpdate {
        constructor(profile: GameProfile): this(profile.username, profile.properties)

        override val type: Type = Type.ADD_PLAYER

        override fun write(buffer: ByteBuf) {
            buffer.writeString(name)
            buffer.writeList(properties, GameProfile.Property::write)
        }

        companion object : NetworkReadable<AddPlayer> {
            override fun read(buffer: ByteBuf): AddPlayer {
                return AddPlayer(buffer.readString(), buffer.readList(GameProfile.Property::read))
            }
        }
    }
}