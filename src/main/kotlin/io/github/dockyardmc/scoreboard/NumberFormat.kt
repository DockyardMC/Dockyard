package io.github.dockyardmc.scoreboard

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.scroll.Component
import io.netty.buffer.ByteBuf

sealed interface NumberFormat {
    fun write(buffer: ByteBuf)
}

data object BlankNumberFormat: NumberFormat {
    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(0)
    }
}

class StyledNumberFormat(
    color: String?,
    font: String?,
    bold: Boolean = false,
    italic: Boolean = false,
    underlined: Boolean = false,
    strikethrough: Boolean = false,
    obfuscated: Boolean = false
): NumberFormat {
    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(1)
        // TODO
    }
}

class FixedNumberFormat(val component: Component): NumberFormat {
    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(2)
        buffer.writeNBT(component.toNBT())
    }
}