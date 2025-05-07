package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class PotionContentsComponent(
    val potion: PotionEffect?,
    val customColor: CustomColor?,
    val effects: List<AppliedPotionEffect>,
    val customName: String?
) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeOptional(potion?.getProtocolId(), ByteBuf::writeVarInt)
        buffer.writeOptional(customColor, CustomColor::writePackedInt)
        buffer.writeList(effects, ByteBuf::writeAppliedPotionEffect)
        buffer.writeOptional(customName, ByteBuf::writeString)
    }

    companion object : NetworkReadable<PotionContentsComponent> {
        override fun read(buffer: ByteBuf): PotionContentsComponent {
            return PotionContentsComponent(
                buffer.readOptional(ByteBuf::readVarInt)?.let { PotionEffectRegistry.getByProtocolId(it) },
                buffer.readOptional(ByteBuf::readInt)?.let { CustomColor.fromRGBInt(it) },
                buffer.readAppliedPotionEffectsList(),
                buffer.readOptional(ByteBuf::readString)
            )
        }
    }
}