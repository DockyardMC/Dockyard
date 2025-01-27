package io.github.dockyardmc.protocol.registry

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writers.*
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

abstract class RegistryEntry: NetworkWritable {
    abstract fun getIdentifier(): String
    abstract fun getNbt(): NBTCompound?

    override fun write(buffer: ByteBuf) {
        buffer.writeString(getIdentifier())
        buffer.writeOptional<NBT>(getNbt(), ByteBuf::writeNamedBinaryTag)
    }

    companion object {
        fun read(buffer: ByteBuf): RegistryEntry {
            val identifier = buffer.readString()
            val nbt = buffer.readOptional(ByteBuf::readNBT)

            return NetworkRegistryEntry(nbt as NBTCompound, identifier)
        }
    }
}