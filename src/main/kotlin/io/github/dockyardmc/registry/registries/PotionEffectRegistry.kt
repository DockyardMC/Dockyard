package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object PotionEffectRegistry: Registry {

    override val identifier: String = "minecraft:potion_effect"

    val potionEffects: MutableMap<String, PotionEffect> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    fun addEntry(entry: PotionEffect) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        potionEffects[entry.identifier] = entry
    }

    init {
        addEntry(PotionEffect("minecraft:speed", "Speed", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:slowness", "Slowness", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:haste", "Haste", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:mining_fatigue", "Mining Fatigue", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:strength", "Strength", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:instant_health", "Instant Health", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:instant_damage", "Instant Damage", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:jump_boost", "Jump Boost", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:nausea", "Nausea", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:regeneration", "Regeneration", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:resistance", "Resistance", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:fire_resistance", "Fire Resistance", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:water_breathing", "Water Breathing", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:invisibility", "Invisibility", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:blindness", "Blindness", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:night_vision", "Night Vision", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:hunger", "Hunger", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:weakness", "Weakness", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:poison", "Poison", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:wither", "Wither", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:health_boost", "Health Boost", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:absorption", "Absorption", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:saturation", "Saturation", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:glowing", "Glowing", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:levitation", "Levitation", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:luck", "Luck", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:unluck", "Bad Luck", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:slow_falling", "Slow Falling", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:conduit_power", "Conduit Power", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:dolphins_grace", "Dolphin's Grace", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:bad_omen", "Bad Omen", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:hero_of_the_village", "Hero of the Village", PotionEffectType.GOOD))
        addEntry(PotionEffect("minecraft:darkness", "Darkness", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:trial_omen", "Trial Omen", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:raid_omen", "Raid Omen", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:wind_charged", "Wind Charged", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:weaving", "Weaving", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:oozing", "Oozing", PotionEffectType.BAD))
        addEntry(PotionEffect("minecraft:infested", "Infested", PotionEffectType.BAD))
    }

    override fun get(identifier: String): PotionEffect {
        return potionEffects[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): PotionEffect? {
        return potionEffects[identifier]
    }

    override fun getByProtocolId(id: Int): PotionEffect {
        return potionEffects.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, PotionEffect> {
        return potionEffects
    }
}

data class PotionEffect(
    val identifier: String,
    val name: String,
    val type: PotionEffectType,
): RegistryEntry {

    override fun getProtocolId(): Int {
        return PotionEffectRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }


    override fun getNbt(): NBTCompound? = null
}

enum class PotionEffectType {
    GOOD,
    BAD
}