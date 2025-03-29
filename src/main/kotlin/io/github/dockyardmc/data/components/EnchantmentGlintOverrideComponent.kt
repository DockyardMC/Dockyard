package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class EnchantmentGlintOverrideComponent(val enchantGlint: Boolean): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(enchantGlint)
    }

    companion object: NetworkReadable<EnchantmentGlintOverrideComponent> {
        override fun read(buffer: ByteBuf): EnchantmentGlintOverrideComponent {
            return EnchantmentGlintOverrideComponent(buffer.readBoolean())
        }
    }
}