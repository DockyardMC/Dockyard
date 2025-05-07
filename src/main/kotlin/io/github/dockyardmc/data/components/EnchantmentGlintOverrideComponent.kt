package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class EnchantmentGlintOverrideComponent(val enchantGlint: Boolean) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(enchantGlint)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofBoolean(enchantGlint))
    }

    companion object : NetworkReadable<EnchantmentGlintOverrideComponent> {
        override fun read(buffer: ByteBuf): EnchantmentGlintOverrideComponent {
            return EnchantmentGlintOverrideComponent(buffer.readBoolean())
        }
    }
}