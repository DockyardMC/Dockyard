package io.github.dockyardmc.protocol.writers

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.scroll.serializers.NbtToComponentSerializer
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound

fun ByteBuf.writeTextComponent(component: Component) {
    component.italic = false
    this.writeNBT(component.toNBT())
}

fun ByteBuf.writeTextComponent(text: String) {
    this.writeTextComponent(text.toComponent())
}

fun ByteBuf.readTextComponent(): Component {
    val nbt = this.readNBT() as NBTCompound
    return NbtToComponentSerializer.serializeNbt(nbt)
}
