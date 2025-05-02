package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object JukeboxSongRegistry : DynamicRegistry {

    override val identifier: String = "minecraft:jukebox_song"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val jukeboxSongs: MutableMap<String, JukeboxSong> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    fun addEntry(entry: JukeboxSong, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        jukeboxSongs[entry.identifier] = entry
        if (updateCache) updateCache()
    }

    override fun register() {
        addEntry(JukeboxSong("minecraft:11", 11, "jukebox_song.minecraft.11", 71.0f, "minecraft:music_disc.11"), false)
        addEntry(JukeboxSong("minecraft:13", 1, "jukebox_song.minecraft.13", 178.0f, "minecraft:music_disc.13"), false)
        addEntry(JukeboxSong("minecraft:5", 15, "jukebox_song.minecraft.5", 178.0f, "minecraft:music_disc.5"), false)
        addEntry(JukeboxSong("minecraft:blocks", 3, "jukebox_song.minecraft.blocks", 345.0f, "minecraft:music_disc.blocks"), false)
        addEntry(JukeboxSong("minecraft:cat", 2, "jukebox_song.minecraft.cat", 185.0f, "minecraft:music_disc.cat"), false)
        addEntry(JukeboxSong("minecraft:chirp", 4, "jukebox_song.minecraft.chirp", 185.0f, "minecraft:music_disc.chirp"), false)
        addEntry(JukeboxSong("minecraft:creator", 12, "jukebox_song.minecraft.creator", 176.0f, "minecraft:music_disc.creator"), false)
        addEntry(JukeboxSong("minecraft:creator_music_box", 11, "jukebox_song.minecraft.creator_music_box", 73.0f, "minecraft:music_disc.creator_music_box"), false)
        addEntry(
            JukeboxSong("minecraft:far", 5, "jukebox_song.minecraft.far", 174.0f, "minecraft:music_disc.far"),
            false
        )
        addEntry(
            JukeboxSong("minecraft:mall", 6, "jukebox_song.minecraft.mall", 197.0f, "minecraft:music_disc.mall"),
            false
        )
        addEntry(
            JukeboxSong(
                "minecraft:mellohi",
                7,
                "jukebox_song.minecraft.mellohi",
                96.0f,
                "minecraft:music_disc.mellohi"
            ), false
        )
        addEntry(
            JukeboxSong(
                "minecraft:otherside",
                14,
                "jukebox_song.minecraft.otherside",
                195.0f,
                "minecraft:music_disc.otherside"
            ), false
        )
        addEntry(
            JukeboxSong(
                "minecraft:pigstep",
                13,
                "jukebox_song.minecraft.pigstep",
                149.0f,
                "minecraft:music_disc.pigstep"
            ), false
        )
        addEntry(
            JukeboxSong(
                "minecraft:precipice",
                13,
                "jukebox_song.minecraft.precipice",
                299.0f,
                "minecraft:music_disc.precipice"
            ), false
        )
        addEntry(
            JukeboxSong(
                "minecraft:relic",
                14,
                "jukebox_song.minecraft.relic",
                218.0f,
                "minecraft:music_disc.relic"
            ), false
        )
        addEntry(
            JukeboxSong("minecraft:stal", 8, "jukebox_song.minecraft.stal", 150.0f, "minecraft:music_disc.stal"),
            false
        )
        addEntry(
            JukeboxSong(
                "minecraft:strad",
                9,
                "jukebox_song.minecraft.strad",
                188.0f,
                "minecraft:music_disc.strad"
            ), false
        )
        addEntry(
            JukeboxSong("minecraft:wait", 12, "jukebox_song.minecraft.wait", 238.0f, "minecraft:music_disc.wait"),
            false
        )
        addEntry(
            JukeboxSong("minecraft:ward", 10, "jukebox_song.minecraft.ward", 251.0f, "minecraft:music_disc.ward"),
            false
        )
        updateCache()
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if (!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): JukeboxSong {
        return jukeboxSongs[identifier]
            ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): JukeboxSong? {
        return jukeboxSongs[identifier]
    }

    override fun getByProtocolId(id: Int): JukeboxSong {
        return jukeboxSongs.values.toList().getOrNull(id)
            ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, JukeboxSong> {
        return jukeboxSongs
    }
}

data class JukeboxSong(
    val identifier: String,
    val comparatorOutput: Int,
    val description: String,
    val lengthInSeconds: Float,
    val sound: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return JukeboxSongRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("comparator_output", comparatorOutput)
            it.put("description", NBT.Compound { desc ->
                desc.put("translate", description)
            })
            it.put("length_in_seconds", lengthInSeconds)
            it.put("sound_event", sound)
        }
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

}