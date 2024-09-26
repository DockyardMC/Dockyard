package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.RegistryEntry
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object PotionEffectRegistry: Registry {

    override val identifier: String = "minecraft:painting_variant"

    val potionEffects: MutableMap<String, PotionEffect> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    init {
        potionEffects["minecraft:speed"] to PotionEffect("minecraft:speed", "Speed", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:slowness"] to PotionEffect("minecraft:slowness", "Slowness", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:haste"] to PotionEffect("minecraft:haste", "Haste", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:mining_fatigue"] to PotionEffect("minecraft:mining_fatigue", "Mining Fatigue", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:strength"] to PotionEffect("minecraft:strength", "Strength", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:instant_health"] to PotionEffect("minecraft:instant_health", "Instant Health", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:instant_damage"] to PotionEffect("minecraft:instant_damage", "Instant Damage", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:jump_boost"] to PotionEffect("minecraft:jump_boost", "Jump Boost", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:nausea"] to PotionEffect("minecraft:nausea", "Nausea", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:regeneration"] to PotionEffect("minecraft:regeneration", "Regeneration", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:resistance"] to PotionEffect("minecraft:resistance", "Resistance", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:fire_resistance"] to PotionEffect("minecraft:fire_resistance", "Fire Resistance", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:water_breathing"] to PotionEffect("minecraft:water_breathing", "Water Breathing", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:invisibility"] to PotionEffect("minecraft:invisibility", "Invisibility", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:blindness"] to PotionEffect("minecraft:blindness", "Blindness", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:night_vision"] to PotionEffect("minecraft:night_vision", "Night Vision", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:hunger"] to PotionEffect("minecraft:hunger", "Hunger", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:weakness"] to PotionEffect("minecraft:weakness", "Weakness", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:poison"] to PotionEffect("minecraft:poison", "Poison", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:wither"] to PotionEffect("minecraft:wither", "Wither", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:health_boost"] to PotionEffect("minecraft:health_boost", "Health Boost", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:absorption"] to PotionEffect("minecraft:absorption", "Absorption", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:saturation"] to PotionEffect("minecraft:saturation", "Saturation", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:glowing"] to PotionEffect("minecraft:glowing", "Glowing", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:levitation"] to PotionEffect("minecraft:levitation", "Levitation", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:luck"] to PotionEffect("minecraft:luck", "Luck", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:unluck"] to PotionEffect("minecraft:unluck", "Bad Luck", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:slow_falling"] to PotionEffect("minecraft:slow_falling", "Slow Falling", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:conduit_power"] to PotionEffect("minecraft:conduit_power", "Conduit Power", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:dolphins_grace"] to PotionEffect("minecraft:dolphins_grace", "Dolphin's Grace", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:bad_omen"] to PotionEffect("minecraft:bad_omen", "Bad Omen", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:hero_of_the_village"] to PotionEffect("minecraft:hero_of_the_village", "Hero of the Village", PotionEffectType.GOOD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:darkness"] to PotionEffect("minecraft:darkness", "Darkness", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:trial_omen"] to PotionEffect("minecraft:trial_omen", "Trial Omen", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:raid_omen"] to PotionEffect("minecraft:raid_omen", "Raid Omen", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:wind_charged"] to PotionEffect("minecraft:wind_charged", "Wind Charged", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:weaving"] to PotionEffect("minecraft:weaving", "Weaving", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:oozing"] to PotionEffect("minecraft:oozing", "Oozing", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
        potionEffects["minecraft:infested"] to PotionEffect("minecraft:infested", "Infested", PotionEffectType.BAD, protocolIdCounter.getAndIncrement())
    }

    override fun get(identifier: String): PotionEffect {
        return potionEffects[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): PotionEffect? {
        return potionEffects[identifier]
    }

    override fun getByProtocolId(id: Int): PotionEffect {
        return potionEffects.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, PotionEffect> {
        return potionEffects
    }
}

data class PotionEffect(val namespace: String, val name: String, val type: PotionEffectType, override val protocolId: Int): RegistryEntry {
    override fun getNbt(): NBTCompound? = null
}

enum class PotionEffectType {
    GOOD,
    BAD
}