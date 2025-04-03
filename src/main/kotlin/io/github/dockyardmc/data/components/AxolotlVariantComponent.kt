package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readEnum
import io.github.dockyardmc.extentions.writeEnum
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

data class AxolotlVariantComponent(val variant: Variant) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeEnum(variant)
    }

    companion object : NetworkReadable<AxolotlVariantComponent> {
        override fun read(buffer: ByteBuf): AxolotlVariantComponent {
            return AxolotlVariantComponent(buffer.readEnum())
        }
    }

    enum class Variant {
        LUCY,
        WILD,
        GOLD,
        CYAN,
        BLUE // <-- rare shiny one
    }
}