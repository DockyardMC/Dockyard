package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.fromRGBInt
import io.github.dockyardmc.extentions.getPackedInt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.CustomColor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PotionEffectRegistry : DataDrivenRegistry<PotionEffect>() {
    override val identifier: String = "minecraft:potion_effect"
}

@Serializable
data class PotionEffect(
    val identifier: String,
    val name: String,
    val type: Type,
    val isInstant: Boolean,
    @Serializable(with = CustomColorIntSerializer::class)
    val color: CustomColor,
    ) : RegistryEntry {

    enum class Type {
        BENEFICIAL,
        HARMFUL,
        NEUTRAL
    }

    override fun getProtocolId(): Int {
        return PotionEffectRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }
}

object CustomColorIntSerializer : KSerializer<CustomColor> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CustomColorInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: CustomColor) {
        encoder.encodeInt(value.getPackedInt())
    }

    override fun deserialize(decoder: Decoder): CustomColor {
        return CustomColor.fromRGBInt(decoder.decodeInt())
    }
}