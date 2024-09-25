package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object DamageTypeRegistry: DynamicRegistry {

    override val identifier: String = "minecraft:damage_type"

    private lateinit var cachedPacket: ClientboundRegistryDataPacket

    val map: MutableMap<String, DamageType> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    init {
        map["minecraft:arrow"] = DamageType(exhaustion = 0.1f, messageId = "arrow", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:bad_respawn_point"] = DamageType(deathMessageType = "intentional_game_design", exhaustion = 0.1f, messageId = "badRespawnPoint", scaling = "always", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:cactus"] = DamageType(exhaustion = 0.1f, messageId = "cactus", scaling = "when_caused_by_living_non_player" , protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:campfire"] = DamageType(effects = "burning", exhaustion = 0.1f, messageId = "inFire",scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:cramming"] = DamageType(exhaustion = 0.0f, messageId = "cramming", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:dragon_breath"] = DamageType(exhaustion = 0.0f, messageId = "dragonBreath", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:drown"] = DamageType(effects = "drowning", exhaustion = 0.0f, messageId = "drown",scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:dry_out"] = DamageType(exhaustion = 0.1f, messageId = "dryout", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:explosion"] = DamageType(exhaustion = 0.1f, messageId = "explosion", scaling = "always", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:fall"] = DamageType(deathMessageType = "fall_variants", exhaustion = 0.0f, messageId = "fall",scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:falling_anvil"] = DamageType(exhaustion = 0.1f, messageId = "anvil", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:falling_block"] = DamageType(exhaustion = 0.1f, messageId = "fallingBlock", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:falling_stalactite"] = DamageType(exhaustion = 0.1f, messageId = "fallingStalactite", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:fireball"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "fireball", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:fireworks"] = DamageType(exhaustion = 0.1f, messageId = "fireworks", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:fly_into_wall"] = DamageType(exhaustion = 0.0f, messageId = "flyIntoWall", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:freeze"] = DamageType(effects = "freezing", exhaustion = 0.0f, scaling = "when_caused_by_living_non_player", messageId = "freeze", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:generic"] = DamageType(exhaustion = 0.0f, messageId = "generic", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:generic_kill"] = DamageType(exhaustion = 0.0f, messageId = "genericKill", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:hot_floor"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "hotFloor", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:in_fire"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "inFire", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:in_wall"] = DamageType(exhaustion = 0.0f, messageId = "inWall", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:indirect_magic"] = DamageType(exhaustion = 0.0f, messageId = "indirectMagic", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:lava"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "lava", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:lightning_bolt"] = DamageType(exhaustion = 0.1f, messageId = "lightningBolt", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:magic"] = DamageType(exhaustion = 0.0f, messageId = "magic", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:mob_attack"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:mob_attack_no_aggro"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:mob_projectile"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:on_fire"] = DamageType(effects = "burning", exhaustion = 0.0f, scaling = "when_caused_by_living_non_player", messageId = "onFire", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:out_of_world"] = DamageType(exhaustion = 0.0f, messageId = "outOfWorld", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:outside_border"] = DamageType(exhaustion = 0.0f, messageId = "outsideBorder", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:player_attack"] = DamageType(exhaustion = 0.1f, messageId = "player", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:player_explosion"] = DamageType(exhaustion = 0.1f, messageId = "explosion.player", scaling = "always", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:sonic_boom"] = DamageType(exhaustion = 0.0f, messageId = "sonic_boom", scaling = "always", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:spit"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:stalagmite"] = DamageType(exhaustion = 0.0f, messageId = "stalagmite", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:starve"] = DamageType(exhaustion = 0.0f, messageId = "starve", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:sting"] = DamageType(exhaustion = 0.1f, messageId = "sting", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:sweet_berry_bush"] = DamageType(effects = "poking", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "sweetBerryBush", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:thorns"] = DamageType(effects = "thorns", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "thorns", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:thrown"] = DamageType(exhaustion = 0.1f, messageId = "thrown", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:trident"] = DamageType(exhaustion = 0.1f, messageId = "trident", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:unattributed_fireball"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "onFire", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:wind_charge"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:wither"] = DamageType(exhaustion = 0.0f, messageId = "wither", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        map["minecraft:wither_skull"] = DamageType(exhaustion = 0.1f, messageId = "witherSkull", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): DamageType {
        return map[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): DamageType? {
        return map[identifier]
    }

    override fun getByProtocolId(id: Int): DamageType {
        return map.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, DamageType> {
        return map
    }
}

data class DamageType(
    val exhaustion: Float,
    val messageId: String,
    val scaling: String,
    val effects: String? = null,
    val deathMessageType: String? = null,
    override val protocolId: Int
): RegistryEntry {
    override fun getNbt(): NBTCompound {
        return NBT.Compound {
            it.put("exhaustion", this.exhaustion)
            it.put("messageId", this.messageId)
            it.put("scaling", this.scaling)
        }
    }
}