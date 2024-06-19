package io.github.dockyardmc.registry

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT

data class Registry(
    val identifier: String,
    val list: MutableList<RegistryEntry>
)

data class RegistryEntry(
    val identifier: String,
    val data: NBT?
)

fun ByteBuf.writeRegistry(registry: Registry) {
    this.writeUtf(registry.identifier)
    this.writeVarInt(registry.list.size)
    registry.list.forEach {
        this.writeUtf(it.identifier)
        val isDataPresent = it.data != null
        this.writeBoolean(isDataPresent)
        if(isDataPresent) this.writeNBT(it.data!!)
    }
}