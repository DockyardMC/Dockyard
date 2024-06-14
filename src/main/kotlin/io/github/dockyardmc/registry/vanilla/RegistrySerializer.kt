package io.github.dockyardmc.registry.vanilla

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT

data class Registry(
    var identifier: String,
    var entries: MutableList<RegistryEntry>
)

data class RegistryEntry(
    val identifier: String,
    val data: NBT?
)

fun ByteBuf.writeRegistry(registry: Registry) {
    this.writeUtf(registry.identifier)
    this.writeVarInt(registry.entries.size)
    registry.entries.forEach {
        this.writeUtf(it.identifier)
        this.writeBoolean(it.data != null)
        if(it.data != null) {
            this.writeNBT(it.data)
        }
    }
}