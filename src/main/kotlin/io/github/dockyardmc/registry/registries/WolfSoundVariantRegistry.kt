package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object WolfSoundVariantRegistry : DataDrivenRegistry<WolfSoundVariant>() {
    override val identifier: String = "minecraft:wolf_sound_variant"
}

@Serializable
data class WolfSoundVariant(
    val identifier: String,
    val ambientSound: String,
    val deathSound: String,
    val growlSound: String,
    val hurtSound: String,
    val pantSound: String,
    val whineSound: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return WolfSoundVariantRegistry.getProtocolIdByEntry(this)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }


    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withString("ambient_sound", ambientSound)
            withString("death_sound", deathSound)
            withString("growl_sound", growlSound)
            withString("hurt_sound", hurtSound)
            withString("pant_sound", pantSound)
            withString("whine_sound", whineSound)
        }
    }
}