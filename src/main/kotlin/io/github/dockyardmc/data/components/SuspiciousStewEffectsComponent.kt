package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readAppliedPotionEffectsList
import io.github.dockyardmc.extentions.writeAppliedPotionEffect
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class SuspiciousStewEffectsComponent(val effects: List<AppliedPotionEffect>) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(effects, ByteBuf::writeAppliedPotionEffect)
    }

    companion object : NetworkReadable<SuspiciousStewEffectsComponent> {
        override fun read(buffer: ByteBuf): SuspiciousStewEffectsComponent {
            return SuspiciousStewEffectsComponent(buffer.readAppliedPotionEffectsList())
        }
    }
}