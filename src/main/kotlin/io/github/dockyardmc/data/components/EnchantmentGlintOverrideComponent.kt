package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class EnchantmentGlintOverrideComponent(val enchantGlint: Boolean): DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(enchantGlint)
    }

    companion object: NetworkReadable<EnchantmentGlintOverrideComponent> {
        override fun read(buffer: ByteBuf): EnchantmentGlintOverrideComponent {
            return EnchantmentGlintOverrideComponent(buffer.readBoolean())
        }
    }
}