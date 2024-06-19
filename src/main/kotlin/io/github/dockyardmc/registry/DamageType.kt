package io.github.dockyardmc.registry

import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

object DamageTypes {

    // Dockyard doesn't use vanilla damage types
    var map = mutableMapOf<String, DamageType>(
        "minecraft:generic" to DamageType(0.0f, "generic", "when_caused_by_living_non_player"),
        "minecraft:generic_kill" to DamageType(0.0f, "genericKill", "when_caused_by_living_non_player"),
    )

    lateinit var registryCache: Registry

    fun cacheRegistry() {
        val registryEntries = mutableListOf<RegistryEntry>()
        map.forEach { registryEntries.add(RegistryEntry(it.key, it.value.toNBT())) }
        registryCache = Registry("minecraft:damage_type", registryEntries)
    }

    val GENERIC = map["minecraft:generic"]!!
    val GENERIC_KILL = map["minecraft:generic_kill"]!!

    init {
        cacheRegistry()
    }
}

data class DamageType(
    val exhaustion: Float,
    val messageId: String,
    val scaling: String
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("exhaustion", this.exhaustion)
            it.put("message_id", this.messageId)
            it.put("scaling", this.scaling)
        }
    }
}