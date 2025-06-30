package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.registries.PotionType
import io.github.dockyardmc.registry.registries.PotionTypeRegistry
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

class PotionContentsComponent(
    val potion: PotionType?,
    val customColor: CustomColor?,
    val effects: List<AppliedPotionEffect>,
    val customName: String?
) : DataComponent() {

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            optional("potion", potion, CRC32CHasher::ofRegistryEntry)
            optional("custom_color", customColor, CRC32CHasher::ofColor)
            defaultStructList("custom_effects", effects, listOf(), AppliedPotionEffect::hashStruct)
            optional("custom_name", customName, CRC32CHasher::ofString)
        }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeOptional(potion?.getProtocolId(), ByteBuf::writeVarInt)
        buffer.writeOptional(customColor, CustomColor::writePackedInt)
        buffer.writeList(effects, AppliedPotionEffect::write)
        buffer.writeOptional(customName, ByteBuf::writeString)
    }

    companion object : NetworkReadable<PotionContentsComponent> {
        override fun read(buffer: ByteBuf): PotionContentsComponent {
            return PotionContentsComponent(
                buffer.readOptional(ByteBuf::readVarInt)?.let { PotionTypeRegistry.getByProtocolId(it) },
                buffer.readOptional(ByteBuf::readInt)?.let { CustomColor.fromRGBInt(it) },
                buffer.readList(AppliedPotionEffect::read),
                buffer.readOptional(ByteBuf::readString)
            )
        }
    }
}