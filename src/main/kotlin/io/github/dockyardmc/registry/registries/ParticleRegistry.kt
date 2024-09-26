package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.RegistryEntry
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object ParticleRegistry : Registry {

    override val identifier: String = "minecraft:particle"

    val particles: MutableMap<String, Particle> = mutableMapOf()
    val protocolIdCounter = AtomicInteger()

    init {
        particles["minecraft:angry_villager"] = Particle("angry_villager", protocolIdCounter.getAndIncrement())
        particles["minecraft:block"] = Particle("block", protocolIdCounter.getAndIncrement())
        particles["minecraft:block_marker"] = Particle("block_marker", protocolIdCounter.getAndIncrement())
        particles["minecraft:bubble"] = Particle("bubble", protocolIdCounter.getAndIncrement())
        particles["minecraft:cloud"] = Particle("cloud", protocolIdCounter.getAndIncrement())
        particles["minecraft:crit"] = Particle("crit", protocolIdCounter.getAndIncrement())
        particles["minecraft:damage_indicator"] = Particle("damage_indicator", protocolIdCounter.getAndIncrement())
        particles["minecraft:dragon_breath"] = Particle("dragon_breath", protocolIdCounter.getAndIncrement())
        particles["minecraft:dripping_lava"] = Particle("dripping_lava", protocolIdCounter.getAndIncrement())
        particles["minecraft:falling_lava"] = Particle("falling_lava", protocolIdCounter.getAndIncrement())
        particles["minecraft:landing_lava"] = Particle("landing_lava", protocolIdCounter.getAndIncrement())
        particles["minecraft:dripping_water"] = Particle("dripping_water", protocolIdCounter.getAndIncrement())
        particles["minecraft:falling_water"] = Particle("falling_water", protocolIdCounter.getAndIncrement())
        particles["minecraft:dust"] = Particle("dust", protocolIdCounter.getAndIncrement())
        particles["minecraft:dust_color_transition"] = Particle("dust_color_transition", protocolIdCounter.getAndIncrement())
        particles["minecraft:effect"] = Particle("effect", protocolIdCounter.getAndIncrement())
        particles["minecraft:elder_guardian"] = Particle("elder_guardian", protocolIdCounter.getAndIncrement())
        particles["minecraft:enchanted_hit"] = Particle("enchanted_hit", protocolIdCounter.getAndIncrement())
        particles["minecraft:enchant"] = Particle("enchant", protocolIdCounter.getAndIncrement())
        particles["minecraft:end_rod"] = Particle("end_rod", protocolIdCounter.getAndIncrement())
        particles["minecraft:entity_effect"] = Particle("entity_effect", protocolIdCounter.getAndIncrement())
        particles["minecraft:explosion_emitter"] = Particle("explosion_emitter", protocolIdCounter.getAndIncrement())
        particles["minecraft:explosion"] = Particle("explosion", protocolIdCounter.getAndIncrement())
        particles["minecraft:gust"] = Particle("gust", protocolIdCounter.getAndIncrement())
        particles["minecraft:small_gust"] = Particle("small_gust", protocolIdCounter.getAndIncrement())
        particles["minecraft:gust_emitter_large"] = Particle("gust_emitter_large", protocolIdCounter.getAndIncrement())
        particles["minecraft:gust_emitter_small"] = Particle("gust_emitter_small", protocolIdCounter.getAndIncrement())
        particles["minecraft:sonic_boom"] = Particle("sonic_boom", protocolIdCounter.getAndIncrement())
        particles["minecraft:falling_dust"] = Particle("falling_dust", protocolIdCounter.getAndIncrement())
        particles["minecraft:firework"] = Particle("firework", protocolIdCounter.getAndIncrement())
        particles["minecraft:fishing"] = Particle("fishing", protocolIdCounter.getAndIncrement())
        particles["minecraft:flame"] = Particle("flame", protocolIdCounter.getAndIncrement())
        particles["minecraft:infested"] = Particle("infested", protocolIdCounter.getAndIncrement())
        particles["minecraft:cherry_leaves"] = Particle("cherry_leaves", protocolIdCounter.getAndIncrement())
        particles["minecraft:sculk_soul"] = Particle("sculk_soul", protocolIdCounter.getAndIncrement())
        particles["minecraft:sculk_charge"] = Particle("sculk_charge", protocolIdCounter.getAndIncrement())
        particles["minecraft:sculk_charge_pop"] = Particle("sculk_charge_pop", protocolIdCounter.getAndIncrement())
        particles["minecraft:soul_fire_flame"] = Particle("soul_fire_flame", protocolIdCounter.getAndIncrement())
        particles["minecraft:soul"] = Particle("soul", protocolIdCounter.getAndIncrement())
        particles["minecraft:flash"] = Particle("flash", protocolIdCounter.getAndIncrement())
        particles["minecraft:happy_villager"] = Particle("happy_villager", protocolIdCounter.getAndIncrement())
        particles["minecraft:composter"] = Particle("composter", protocolIdCounter.getAndIncrement())
        particles["minecraft:heart"] = Particle("heart", protocolIdCounter.getAndIncrement())
        particles["minecraft:instant_effect"] = Particle("instant_effect", protocolIdCounter.getAndIncrement())
        particles["minecraft:item"] = Particle("item", protocolIdCounter.getAndIncrement())
        particles["minecraft:vibration"] = Particle("vibration", protocolIdCounter.getAndIncrement())
        particles["minecraft:item_slime"] = Particle("item_slime", protocolIdCounter.getAndIncrement())
        particles["minecraft:item_cobweb"] = Particle("item_cobweb", protocolIdCounter.getAndIncrement())
        particles["minecraft:item_snowball"] = Particle("item_snowball", protocolIdCounter.getAndIncrement())
        particles["minecraft:large_smoke"] = Particle("large_smoke", protocolIdCounter.getAndIncrement())
        particles["minecraft:lava"] = Particle("lava", protocolIdCounter.getAndIncrement())
        particles["minecraft:mycelium"] = Particle("mycelium", protocolIdCounter.getAndIncrement())
        particles["minecraft:note"] = Particle("note", protocolIdCounter.getAndIncrement())
        particles["minecraft:poof"] = Particle("poof", protocolIdCounter.getAndIncrement())
        particles["minecraft:portal"] = Particle("portal", protocolIdCounter.getAndIncrement())
        particles["minecraft:rain"] = Particle("rain", protocolIdCounter.getAndIncrement())
        particles["minecraft:smoke"] = Particle("smoke", protocolIdCounter.getAndIncrement())
        particles["minecraft:white_smoke"] = Particle("white_smoke", protocolIdCounter.getAndIncrement())
        particles["minecraft:sneeze"] = Particle("sneeze", protocolIdCounter.getAndIncrement())
        particles["minecraft:spit"] = Particle("spit", protocolIdCounter.getAndIncrement())
        particles["minecraft:squid_ink"] = Particle("squid_ink", protocolIdCounter.getAndIncrement())
        particles["minecraft:sweep_attack"] = Particle("sweep_attack", protocolIdCounter.getAndIncrement())
        particles["minecraft:totem_of_undying"] = Particle("totem_of_undying", protocolIdCounter.getAndIncrement())
        particles["minecraft:underwater"] = Particle("underwater", protocolIdCounter.getAndIncrement())
        particles["minecraft:splash"] = Particle("splash", protocolIdCounter.getAndIncrement())
        particles["minecraft:witch"] = Particle("witch", protocolIdCounter.getAndIncrement())
        particles["minecraft:bubble_pop"] = Particle("bubble_pop", protocolIdCounter.getAndIncrement())
        particles["minecraft:current_down"] = Particle("current_down", protocolIdCounter.getAndIncrement())
        particles["minecraft:bubble_column_up"] = Particle("bubble_column_up", protocolIdCounter.getAndIncrement())
        particles["minecraft:nautilus"] = Particle("nautilus", protocolIdCounter.getAndIncrement())
        particles["minecraft:dolphin"] = Particle("dolphin", protocolIdCounter.getAndIncrement())
        particles["minecraft:campfire_cosy_smoke"] = Particle("campfire_cosy_smoke", protocolIdCounter.getAndIncrement())
        particles["minecraft:campfire_signal_smoke"] = Particle("campfire_signal_smoke", protocolIdCounter.getAndIncrement())
        particles["minecraft:dripping_honey"] = Particle("dripping_honey", protocolIdCounter.getAndIncrement())
        particles["minecraft:falling_honey"] = Particle("falling_honey", protocolIdCounter.getAndIncrement())
        particles["minecraft:landing_honey"] = Particle("landing_honey", protocolIdCounter.getAndIncrement())
        particles["minecraft:falling_nectar"] = Particle("falling_nectar", protocolIdCounter.getAndIncrement())
        particles["minecraft:falling_spore_blossom"] = Particle("falling_spore_blossom", protocolIdCounter.getAndIncrement())
        particles["minecraft:ash"] = Particle("ash", protocolIdCounter.getAndIncrement())
        particles["minecraft:crimson_spore"] = Particle("crimson_spore", protocolIdCounter.getAndIncrement())
        particles["minecraft:warped_spore"] = Particle("warped_spore", protocolIdCounter.getAndIncrement())
        particles["minecraft:spore_blossom_air"] = Particle("spore_blossom_air", protocolIdCounter.getAndIncrement())
        particles["minecraft:dripping_obsidian_tear"] = Particle("dripping_obsidian_tear", protocolIdCounter.getAndIncrement())
        particles["minecraft:falling_obsidian_tear"] = Particle("falling_obsidian_tear", protocolIdCounter.getAndIncrement())
        particles["minecraft:landing_obsidian_tear"] = Particle("landing_obsidian_tear", protocolIdCounter.getAndIncrement())
        particles["minecraft:reverse_portal"] = Particle("reverse_portal", protocolIdCounter.getAndIncrement())
        particles["minecraft:white_ash"] = Particle("white_ash", protocolIdCounter.getAndIncrement())
        particles["minecraft:small_flame"] = Particle("small_flame", protocolIdCounter.getAndIncrement())
        particles["minecraft:snowflake"] = Particle("snowflake", protocolIdCounter.getAndIncrement())
        particles["minecraft:dripping_dripstone_lava"] = Particle("dripping_dripstone_lava", protocolIdCounter.getAndIncrement())
        particles["minecraft:falling_dripstone_lava"] = Particle("falling_dripstone_lava", protocolIdCounter.getAndIncrement())
        particles["minecraft:dripping_dripstone_water"] = Particle("dripping_dripstone_water", protocolIdCounter.getAndIncrement())
        particles["minecraft:falling_dripstone_water"] = Particle("falling_dripstone_water", protocolIdCounter.getAndIncrement())
        particles["minecraft:glow_squid_ink"] = Particle("glow_squid_ink", protocolIdCounter.getAndIncrement())
        particles["minecraft:glow"] = Particle("glow", protocolIdCounter.getAndIncrement())
        particles["minecraft:wax_on"] = Particle("wax_on", protocolIdCounter.getAndIncrement())
        particles["minecraft:wax_off"] = Particle("wax_off", protocolIdCounter.getAndIncrement())
        particles["minecraft:electric_spark"] = Particle("electric_spark", protocolIdCounter.getAndIncrement())
        particles["minecraft:scrape"] = Particle("scrape", protocolIdCounter.getAndIncrement())
        particles["minecraft:shriek"] = Particle("shriek", protocolIdCounter.getAndIncrement())
        particles["minecraft:egg_crack"] = Particle("egg_crack", protocolIdCounter.getAndIncrement())
        particles["minecraft:dust_plume"] = Particle("dust_plume", protocolIdCounter.getAndIncrement())
        particles["minecraft:trial_spawner_detection"] = Particle("trial_spawner_detection", protocolIdCounter.getAndIncrement())
        particles["minecraft:trial_spawner_detection_ominous"] = Particle("trial_spawner_detection_ominous", protocolIdCounter.getAndIncrement())
        particles["minecraft:vault_connection"] = Particle("vault_connection", protocolIdCounter.getAndIncrement())
        particles["minecraft:dust_pillar"] = Particle("dust_pillar", protocolIdCounter.getAndIncrement())
        particles["minecraft:ominous_spawning"] = Particle("ominous_spawning", protocolIdCounter.getAndIncrement())
        particles["minecraft:raid_omen"] = Particle("raid_omen", protocolIdCounter.getAndIncrement())
        particles["minecraft:trial_omen"] = Particle("trial_omen", protocolIdCounter.getAndIncrement())
    }

    override fun get(identifier: String): Particle {
        return particles[identifier]
            ?: throw IllegalStateException("There is no registry entry with identifier $identifier")
    }

    override fun getOrNull(identifier: String): Particle? {
        return particles[identifier]
    }

    override fun getByProtocolId(id: Int): Particle {
        return particles.values.toList().getOrNull(id)
            ?: throw IllegalStateException("There is no registry entry with protocol id $id")
    }

    override fun getMap(): Map<String, Particle> {
        return particles
    }
}

data class Particle(
    val identifier: String,
    override val protocolId: Int,
) : RegistryEntry {
    override fun getNbt(): NBTCompound? = null
}