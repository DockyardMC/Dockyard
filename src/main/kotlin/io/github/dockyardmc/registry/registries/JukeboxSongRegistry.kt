package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object JukeboxSongRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:jukebox_song"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val jukeboxSongs: MutableMap<String, JukeboxSong> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    override fun register() {
        jukeboxSongs["minecraft:11"] = JukeboxSong(11, "jukebox_song.minecraft.11", 71.0f, "minecraft:music_disc.11", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:13"] = JukeboxSong(1, "jukebox_song.minecraft.13", 178.0f, "minecraft:music_disc.13", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:5"] = JukeboxSong(15, "jukebox_song.minecraft.5", 178.0f, "minecraft:music_disc.5", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:blocks"] = JukeboxSong(3, "jukebox_song.minecraft.blocks", 345.0f, "minecraft:music_disc.blocks", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:cat"] = JukeboxSong(2, "jukebox_song.minecraft.cat", 185.0f, "minecraft:music_disc.cat", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:chirp"] = JukeboxSong(4, "jukebox_song.minecraft.chirp", 185.0f, "minecraft:music_disc.chirp", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:creator"] = JukeboxSong(12, "jukebox_song.minecraft.creator", 176.0f, "minecraft:music_disc.creator", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:creator_music_box"] = JukeboxSong(11, "jukebox_song.minecraft.creator_music_box", 73.0f, "minecraft:music_disc.creator_music_box", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:far"] = JukeboxSong(5, "jukebox_song.minecraft.far", 174.0f, "minecraft:music_disc.far", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:mall"] = JukeboxSong(6, "jukebox_song.minecraft.mall", 197.0f, "minecraft:music_disc.mall", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:mellohi"] = JukeboxSong(7, "jukebox_song.minecraft.mellohi", 96.0f, "minecraft:music_disc.mellohi", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:otherside"] = JukeboxSong(14, "jukebox_song.minecraft.otherside", 195.0f, "minecraft:music_disc.otherside", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:pigstep"] = JukeboxSong(13, "jukebox_song.minecraft.pigstep", 149.0f, "minecraft:music_disc.pigstep", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:precipice"] = JukeboxSong(13, "jukebox_song.minecraft.precipice", 299.0f, "minecraft:music_disc.precipice", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:relic"] = JukeboxSong(14, "jukebox_song.minecraft.relic", 218.0f, "minecraft:music_disc.relic", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:stal"] = JukeboxSong(8, "jukebox_song.minecraft.stal", 150.0f, "minecraft:music_disc.stal", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:strad"] = JukeboxSong(9, "jukebox_song.minecraft.strad", 188.0f, "minecraft:music_disc.strad", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:wait"] = JukeboxSong(12, "jukebox_song.minecraft.wait", 238.0f, "minecraft:music_disc.wait", protocolIdCounter.getAndIncrement())
        jukeboxSongs["minecraft:ward"] = JukeboxSong(10, "jukebox_song.minecraft.ward", 251.0f, "minecraft:music_disc.ward", protocolIdCounter.getAndIncrement())
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): JukeboxSong {
        return jukeboxSongs[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): JukeboxSong? {
        return jukeboxSongs[identifier]
    }

    override fun getByProtocolId(id: Int): JukeboxSong {
        return jukeboxSongs.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, JukeboxSong> {
        return jukeboxSongs
    }
}

data class JukeboxSong(
    val comparatorOutput: Int,
    val description: String,
    val lengthInSeconds: Float,
    val sound: String,
    override val protocolId: Int
): RegistryEntry {

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
}