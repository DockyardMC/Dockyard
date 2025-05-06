package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.HashList
import io.github.dockyardmc.extentions.readNBTCompound
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class BeesComponent(val bees: List<Bee>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(bees, Bee::write)
    }

    override fun hashStruct(): HashHolder {
        return HashList(bees.map { bee -> bee.hashStruct() })
    }

    companion object : NetworkReadable<BeesComponent> {
        override fun read(buffer: ByteBuf): BeesComponent {
            return BeesComponent(buffer.readList(Bee::read))
        }
    }

    data class Bee(val entityData: NBTCompound, val ticksInHive: Int, val minTicksInHive: Int) : NetworkWritable, DataComponentHashable {

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

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("entity_data", 0) //TODO(AW FUCK I FORGOT ABOUT NBT)
                static("ticks_in_hive", CRC32CHasher.ofInt(ticksInHive))
                static("min_ticks_in_hive", CRC32CHasher.ofInt(minTicksInHive))
            }
        }
    }
}