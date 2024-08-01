package io.github.dockyardmc.registry

import cz.lukynka.prettylog.log
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.utils.Resources
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.parser.SNBTParser
import java.io.StringReader

object DamageTypes {

    val map by lazy {
        val vanillaEntry = Resources.getFile("vanillaregistry/damage_type.snbt").split("\n")
        val list = mutableListOf<DamageType>()
        vanillaEntry.forEach { pattern ->
            val split = pattern.split(";")
            val identifier = split[0]
            val sNBT = split[1]

            val nbt = (SNBTParser(StringReader(sNBT))).parse() as NBTCompound
            list.add(DamageType.read(identifier, nbt))
        }
        list.associateBy { it.identifier }
    }

    val ARROW = map["minecraft:arrow"]!!
    val BAD_RESPAWN_POINT = map["minecraft:bad_respawn_point"]!!
    val CACTUS = map["minecraft:cactus"]!!
    val CAMPFIRE = map["minecraft:campfire"]!!
    val CRAMMING = map["minecraft:cramming"]!!
    val DRAGON_BREATH = map["minecraft:dragon_breath"]!!
    val DROWN = map["minecraft:drown"]!!
    val DRY_OUT = map["minecraft:dry_out"]!!
    val EXPLOSION = map["minecraft:explosion"]!!
    val FALL = map["minecraft:fall"]!!
    val FALLING_ANVIL = map["minecraft:falling_anvil"]!!
    val FALLING_BLOCK = map["minecraft:falling_block"]!!
    val FALLING_STALACTITE = map["minecraft:falling_stalactite"]!!
    val FIREBALL = map["minecraft:fireball"]!!
    val FIREWORKS = map["minecraft:fireworks"]!!
    val FLY_INTO_WALL = map["minecraft:fly_into_wall"]!!
    val FREEZE = map["minecraft:freeze"]!!
    val GENERIC = map["minecraft:generic"]!!
    val GENERIC_KILL = map["minecraft:generic_kill"]!!
    val HOT_FLOOR = map["minecraft:hot_floor"]!!
    val IN_FIRE = map["minecraft:in_fire"]!!
    val IN_WALL = map["minecraft:in_wall"]!!
    val INDIRECT_MAGIC = map["minecraft:indirect_magic"]!!
    val LAVA = map["minecraft:lava"]!!
    val LIGHTNING_BOLT = map["minecraft:lightning_bolt"]!!
    val MAGIC = map["minecraft:magic"]!!
    val MOB_ATTACK = map["minecraft:mob_attack"]!!
    val MOB_ATTACK_NO_AGGRO = map["minecraft:mob_attack_no_aggro"]!!
    val MOB_PROJECTILE = map["minecraft:mob_projectile"]!!
    val ON_FIRE = map["minecraft:on_fire"]!!
    val OUT_OF_WORLD = map["minecraft:out_of_world"]!!
    val OUTSIDE_BORDER = map["minecraft:outside_border"]!!
    val PLAYER_ATTACK = map["minecraft:player_attack"]!!
    val PLAYER_EXPLOSION = map["minecraft:player_explosion"]!!
    val SONIC_BOOM = map["minecraft:sonic_boom"]!!
    val SPIT = map["minecraft:spit"]!!
    val STALAGMITE = map["minecraft:stalagmite"]!!
    val STARVE = map["minecraft:starve"]!!
    val STING = map["minecraft:sting"]!!
    val SWEET_BERRY_BUSH = map["minecraft:sweet_berry_bush"]!!
    val THORNS = map["minecraft:thorns"]!!
    val THROWN = map["minecraft:thrown"]!!
    val TRIDENT = map["minecraft:trident"]!!
    val UNATTRIBUTED_FIREBALL = map["minecraft:unattributed_fireball"]!!
    val WIND_CHARGE = map["minecraft:wind_charge"]!!
    val WITHER = map["minecraft:wither"]!!
    val WITHER_SKULL = map["minecraft:wither_skull"]!!

    lateinit var registryCache: Registry

    fun cacheRegistry() {
        val registryEntries = mutableListOf<RegistryEntry>()
        map.forEach { registryEntries.add(RegistryEntry(it.key, it.value.toNBT())) }
        registryCache = Registry("minecraft:damage_type", registryEntries)
    }

    init {
        cacheRegistry()
    }
}

data class DamageType(
    val identifier: String,
    val exhaustion: Float,
    val messageId: String,
    val scaling: String,
    val effects: String?,
    val deathMessageType: String?
) {
    fun toNBT(): NBTCompound {
        return NBT.Compound {
            it.put("exhaustion", this.exhaustion)
            it.put("message_id", this.messageId)
            it.put("scaling", this.scaling)
        }
    }

    val id: Int get() = DamageTypes.map.values.indexOf(this)

    companion object {
        fun read(identifier: String, nbt: NBTCompound): DamageType {
            return DamageType(
                identifier = identifier,
                exhaustion = nbt.getFloat("exhaustion")!!,
                messageId = nbt.getString("message_id")!!,
                scaling = nbt.getString("scaling")!!,
                effects = nbt.getString("effects"),
                deathMessageType = nbt.getString("death_message_Type")
            )
        }
    }
}