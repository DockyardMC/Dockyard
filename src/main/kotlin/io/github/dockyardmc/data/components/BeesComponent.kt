package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readNBTCompound
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class BeesComponent(val bees: List<Bee>) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(bees, Bee::write)
    }

    companion object : NetworkReadable<BeesComponent> {
        override fun read(buffer: ByteBuf): BeesComponent {
            return BeesComponent(buffer.readList(Bee::read))
        }
    }

    data class Bee(val entityData: NBTCompound, val ticksInHive: Int, val minTicksInHive: Int) : NetworkWritable {

        override fun write(buffer: ByteBuf) {
            buffer.writeNBT(entityData)
            buffer.writeVarInt(ticksInHive)
            buffer.writeVarInt(minTicksInHive)
        }

        companion object : NetworkReadable<Bee> {
            override fun read(buffer: ByteBuf): Bee {
                return Bee(buffer.readNBTCompound(), buffer.readVarInt(), buffer.readVarInt())
            }
        }

    }
}