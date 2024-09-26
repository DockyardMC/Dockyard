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

    val damageTypes: MutableMap<String, DamageType> = mutableMapOf()
    val protocolIdCounter =  AtomicInteger()

    init {
        damageTypes["minecraft:arrow"] = DamageType(exhaustion = 0.1f, messageId = "arrow", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:bad_respawn_point"] = DamageType(deathMessageType = "intentional_game_design", exhaustion = 0.1f, messageId = "badRespawnPoint", scaling = "always", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:cactus"] = DamageType(exhaustion = 0.1f, messageId = "cactus", scaling = "when_caused_by_living_non_player" , protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:campfire"] = DamageType(effects = "burning", exhaustion = 0.1f, messageId = "inFire",scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:cramming"] = DamageType(exhaustion = 0.0f, messageId = "cramming", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:dragon_breath"] = DamageType(exhaustion = 0.0f, messageId = "dragonBreath", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:drown"] = DamageType(effects = "drowning", exhaustion = 0.0f, messageId = "drown",scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:dry_out"] = DamageType(exhaustion = 0.1f, messageId = "dryout", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:explosion"] = DamageType(exhaustion = 0.1f, messageId = "explosion", scaling = "always", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:fall"] = DamageType(deathMessageType = "fall_variants", exhaustion = 0.0f, messageId = "fall",scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:falling_anvil"] = DamageType(exhaustion = 0.1f, messageId = "anvil", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:falling_block"] = DamageType(exhaustion = 0.1f, messageId = "fallingBlock", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:falling_stalactite"] = DamageType(exhaustion = 0.1f, messageId = "fallingStalactite", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:fireball"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "fireball", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:fireworks"] = DamageType(exhaustion = 0.1f, messageId = "fireworks", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:fly_into_wall"] = DamageType(exhaustion = 0.0f, messageId = "flyIntoWall", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:freeze"] = DamageType(effects = "freezing", exhaustion = 0.0f, scaling = "when_caused_by_living_non_player", messageId = "freeze", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:generic"] = DamageType(exhaustion = 0.0f, messageId = "generic", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:generic_kill"] = DamageType(exhaustion = 0.0f, messageId = "genericKill", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:hot_floor"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "hotFloor", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:in_fire"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "inFire", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:in_wall"] = DamageType(exhaustion = 0.0f, messageId = "inWall", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:indirect_magic"] = DamageType(exhaustion = 0.0f, messageId = "indirectMagic", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:lava"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "lava", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:lightning_bolt"] = DamageType(exhaustion = 0.1f, messageId = "lightningBolt", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:magic"] = DamageType(exhaustion = 0.0f, messageId = "magic", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:mob_attack"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:mob_attack_no_aggro"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:mob_projectile"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:on_fire"] = DamageType(effects = "burning", exhaustion = 0.0f, scaling = "when_caused_by_living_non_player", messageId = "onFire", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:out_of_world"] = DamageType(exhaustion = 0.0f, messageId = "outOfWorld", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:outside_border"] = DamageType(exhaustion = 0.0f, messageId = "outsideBorder", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:player_attack"] = DamageType(exhaustion = 0.1f, messageId = "player", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:player_explosion"] = DamageType(exhaustion = 0.1f, messageId = "explosion.player", scaling = "always", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:sonic_boom"] = DamageType(exhaustion = 0.0f, messageId = "sonic_boom", scaling = "always", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:spit"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:stalagmite"] = DamageType(exhaustion = 0.0f, messageId = "stalagmite", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:starve"] = DamageType(exhaustion = 0.0f, messageId = "starve", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:sting"] = DamageType(exhaustion = 0.1f, messageId = "sting", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:sweet_berry_bush"] = DamageType(effects = "poking", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "sweetBerryBush", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:thorns"] = DamageType(effects = "thorns", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "thorns", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:thrown"] = DamageType(exhaustion = 0.1f, messageId = "thrown", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:trident"] = DamageType(exhaustion = 0.1f, messageId = "trident", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:unattributed_fireball"] = DamageType(effects = "burning", exhaustion = 0.1f, scaling = "when_caused_by_living_non_player", messageId = "onFire", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:wind_charge"] = DamageType(exhaustion = 0.1f, messageId = "mob", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:wither"] = DamageType(exhaustion = 0.0f, messageId = "wither", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
        damageTypes["minecraft:wither_skull"] = DamageType(exhaustion = 0.1f, messageId = "witherSkull", scaling = "when_caused_by_living_non_player", protocolId = protocolIdCounter.getAndIncrement())
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        if(!::cachedPacket.isInitialized) updateCache()
        return cachedPacket
    }

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }

    override fun get(identifier: String): DamageType {
        return damageTypes[identifier] ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): DamageType? {
        return damageTypes[identifier]
    }

    override fun getByProtocolId(id: Int): DamageType {
        return damageTypes.values.toList().getOrNull(id) ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, DamageType> {
        return damageTypes
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