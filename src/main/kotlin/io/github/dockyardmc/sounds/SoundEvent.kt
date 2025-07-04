package io.github.dockyardmc.sounds

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.protocol.*
import io.github.dockyardmc.registry.registries.SoundRegistry
import io.netty.buffer.ByteBuf
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag

interface SoundEvent : NetworkWritable, NbtWritable, DataComponentHashable {
    companion object {
        fun read(buffer: ByteBuf): SoundEvent {
            val id = buffer.readVarInt() - 1
            if (id != -1) {
                val sound = SoundRegistry.getByProtocolId(id)
                return BuiltinSoundEvent(sound, id)
            }

            val identifier = buffer.readString()
            val range = buffer.readOptional(ByteBuf::readFloat)
            return CustomSoundEvent(identifier, range)
        }
    }

    val identifier: String
}

data class BuiltinSoundEvent(override val identifier: String, val id: Int) : SoundEvent {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(id + 1)
    }

    override fun getNbt(): BinaryTag {
        return StringBinaryTag.stringBinaryTag(identifier)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofString(identifier))
    }

    companion object {
        fun of(identifier: String): BuiltinSoundEvent {
            val id = SoundRegistry[identifier]
            return BuiltinSoundEvent(identifier, id)
        }
    }
}

data class CustomSoundEvent(override val identifier: String, val range: Float? = null) : SoundEvent {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(0)
        buffer.writeString(identifier)
        buffer.writeOptional<Float>(range, ByteBuf::writeFloat)
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("sound_id", identifier)
            if (range != null) withFloat("range", range)
        }
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("sound_id", CRC32CHasher.ofString(identifier))
            optional("range", range, CRC32CHasher::ofFloat)
        }
    }
}