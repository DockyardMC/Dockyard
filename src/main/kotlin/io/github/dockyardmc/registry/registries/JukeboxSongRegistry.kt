package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.utils.kotlinx.ComponentToJsonSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.CompoundBinaryTag

object JukeboxSongRegistry : DataDrivenRegistry<JukeboxSong>() {
    override val identifier: String = "minecraft:jukebox_song"
}

@Serializable
data class JukeboxSong(
    val identifier: String,
    val comparatorOutput: Int,
    @Serializable(ComponentToJsonSerializer::class)
    val description: Component,
    val lengthInSeconds: Float,
    val sound: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return JukeboxSongRegistry.getProtocolEntries().getByValue(this)
    }

    override fun getNbt(): CompoundBinaryTag {
        return nbt {
            withInt("comparator_output", comparatorOutput)
            withCompound("description", description.toNBT())
            withFloat("length_in_seconds", lengthInSeconds)
            withString("sound_event", sound)
        }
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

}