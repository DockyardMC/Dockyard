package io.github.dockyardmc.protocol.registry

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

abstract class RegistryEntry: NetworkWritable {
    abstract fun getIdentifier(): String
    abstract fun getNbt(): NBTCompound?
    abstract fun getProtocolId(): Int

    override fun write(buffer: ByteBuf) {
        buffer.writeString(getIdentifier())
        buffer.writeOptional<NBT>(getNbt(), ByteBuf::writeNamedBinaryTag)
    }

    abstract fun fromNbt(identifier: String, nbt: NBTCompound?): RegistryEntry

    companion object {
        fun read(buffer: ByteBuf): WrappedRegistryEntry {
            val identifier = buffer.readString()
            val nbt = buffer.readOptional(ByteBuf::readNBT) as NBTCompound

            return WrappedRegistryEntry(identifier, nbt)
        }
    }

    data class WrappedRegistryEntry(val identifier: String, val nbt: NBTCompound?)
}