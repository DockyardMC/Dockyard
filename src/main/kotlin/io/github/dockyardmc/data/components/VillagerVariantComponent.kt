package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

data class VillagerVariantComponent(val type: Type) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(type)
    }

    companion object : NetworkReadable<VillagerVariantComponent> {
        override fun read(buffer: ByteBuf): VillagerVariantComponent {
            return VillagerVariantComponent(buffer.readEnum())
        }
    }

    enum class Type(val identifier: String) {
        DESERT("minecraft:desert"),
        JUNGLE("minecraft:jungle"),
        PLAINS("minecraft:plains"),
        SAVANNA("minecraft:savanna"),
        SNOW("minecraft:snow"),
        SWAMP("minecraft:swamp"),
        TAIGA("minecraft:taiga")
    }
}