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

object DamageTypeRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:damage_type"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val damageTypes: MutableMap<String, DamageType> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter =  AtomicInteger()

    fun addEntry(entry: DamageType, updateCache: Boolean = true) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        damageTypes[entry.identifier] = entry
        if(updateCache) updateCache()
    }

    override fun register() {
        addEntry(DamageType("minecraft:arrow", exhaustion = 0.1f, messageId = "arrow", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:bad_respawn_point", deathMessageType = "intentional_game_design", exhaustion = 0.1f, messageId = "badRespawnPoint", scaling = "always"), false)
        addEntry(DamageType("minecraft:cactus", exhaustion = 0.1f, messageId = "cactus", scaling = "when_caused_by_living_non_player" ), false)
        addEntry(DamageType("minecraft:campfire", effects = "burning", exhaustion = 0.1f, messageId = "inFire",scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:cramming", exhaustion = 0.0f, messageId = "cramming", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:dragon_breath", exhaustion = 0.0f, messageId = "dragonBreath", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:drown", effects = "drowning", exhaustion = 0.0f, messageId = "drown",scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:dry_out", exhaustion = 0.1f, messageId = "dryout", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:explosion", exhaustion = 0.1f, messageId = "explosion", scaling = "always"), false)
        addEntry(DamageType("minecraft:fall", deathMessageType = "fall_variants", exhaustion = 0.0f, messageId = "fall",scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:falling_anvil", exhaustion = 0.1f, messageId = "anvil", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:falling_block", exhaustion = 0.1f, messageId = "fallingBlock", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:falling_stalactite", exhaustion = 0.1f, messageId = "fallingStalactite", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:fireball", effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "fireball"), false)
        addEntry(DamageType("minecraft:fireworks", exhaustion = 0.1f, messageId = "fireworks", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:fly_into_wall", exhaustion = 0.0f, messageId = "flyIntoWall", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:freeze", effects = "freezing", exhaustion = 0.0f, scaling = "when_caused_by_living_non_player", messageId = "freeze"), false)
        addEntry(DamageType("minecraft:generic", exhaustion = 0.0f, messageId = "generic", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:generic_kill", exhaustion = 0.0f, messageId = "genericKill", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:hot_floor", effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "hotFloor"), false)
        addEntry(DamageType("minecraft:in_fire", effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "inFire"), false)
        addEntry(DamageType("minecraft:in_wall", exhaustion = 0.0f, messageId = "inWall", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:indirect_magic", exhaustion = 0.0f, messageId = "indirectMagic", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:lava", effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "lava"), false)
        addEntry(DamageType("minecraft:lightning_bolt", exhaustion = 0.1f, messageId = "lightningBolt", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:magic", exhaustion = 0.0f, messageId = "magic", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:mob_attack", exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:mob_attack_no_aggro", exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:mob_projectile", exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:on_fire", effects = "burning", exhaustion = 0.0f, scaling = "when_caused_by_living_non_player", messageId = "onFire"), false)
        addEntry(DamageType("minecraft:out_of_world", exhaustion = 0.0f, messageId = "outOfWorld", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:outside_border", exhaustion = 0.0f, messageId = "outsideBorder", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:player_attack", exhaustion = 0.1f, messageId = "player", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:player_explosion", exhaustion = 0.1f, messageId = "explosion.player", scaling = "always"), false)
        addEntry(DamageType("minecraft:sonic_boom", exhaustion = 0.0f, messageId = "sonic_boom", scaling = "always"), false)
        addEntry(DamageType("minecraft:spit", exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:stalagmite", exhaustion = 0.0f, messageId = "stalagmite", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:starve", exhaustion = 0.0f, messageId = "starve", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:sting", exhaustion = 0.1f, messageId = "sting", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:sweet_berry_bush", effects = "poking", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "sweetBerryBush"), false)
        addEntry(DamageType("minecraft:thorns", effects = "thorns", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "thorns"), false)
        addEntry(DamageType("minecraft:thrown", exhaustion = 0.1f, messageId = "thrown", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:trident", exhaustion = 0.1f, messageId = "trident", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:unattributed_fireball", effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "onFire"), false)
        addEntry(DamageType("minecraft:wind_charge", exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:wither", exhaustion = 0.0f, messageId = "wither", scaling = "when_caused_by_living_non_player"), false)
        addEntry(DamageType("minecraft:wither_skull", exhaustion = 0.1f, messageId = "witherSkull", scaling = "when_caused_by_living_non_player"), false)
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): DamageType {
        return damageTypes[identifier] ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): DamageType? {
        return damageTypes[identifier]
    }

    override fun getByProtocolId(id: Int): DamageType {
        return damageTypes.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, DamageType> {
        return damageTypes
    }
}

data class DamageType(
    val identifier: String,
    val exhaustion: Float,
    val messageId: String,
    val scaling: String,
    val effects: String? = null,
    val deathMessageType: String? = null,
): RegistryEntry {

    override fun getProtocolId(): Int {
        return DamageTypeRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("exhaustion", this.exhaustion)
            it.put("message_id", this.messageId)
            it.put("scaling", this.scaling)
        }
    }
}