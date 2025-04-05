package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.scroll.serializers.NbtToComponentSerializer
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class CustomNameComponent(val component: Component): DataComponent() {

    constructor(name: String): this(name.toComponent())

    override fun write(buffer: ByteBuf) {
        buffer.writeTextComponent(component)
    }

    companion object: NetworkReadable<CustomNameComponent> {
        override fun read(buffer: ByteBuf): CustomNameComponent {
            return CustomNameComponent(NbtToComponentSerializer.serializeNbt(buffer.readNBT() as NBTCompound))
        }
    }
}