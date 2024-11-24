package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.getOrThrow
import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.concurrent.atomic.AtomicInteger

object ParticleRegistry : Registry {

    override val identifier: String = "minecraft:particle"

    val particles: MutableMap<String, Particle> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    private val protocolIdCounter = AtomicInteger()

    fun addEntry(entry: Particle) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        particles[entry.identifier] = entry
    }

    init {
        addEntry(Particle("minecraft:angry_villager"))
        addEntry(Particle("minecraft:block"))
        addEntry(Particle("minecraft:block_marker"))
        addEntry(Particle("minecraft:bubble"))
        addEntry(Particle("minecraft:cloud"))
        addEntry(Particle("minecraft:crit"))
        addEntry(Particle("minecraft:damage_indicator"))
        addEntry(Particle("minecraft:dragon_breath"))
        addEntry(Particle("minecraft:dripping_lava"))
        addEntry(Particle("minecraft:falling_lava"))
        addEntry(Particle("minecraft:landing_lava"))
        addEntry(Particle("minecraft:dripping_water"))
        addEntry(Particle("minecraft:falling_water"))
        addEntry(Particle("minecraft:dust"))
        addEntry(Particle("minecraft:dust_color_transition"))
        addEntry(Particle("minecraft:effect"))
        addEntry(Particle("minecraft:elder_guardian"))
        addEntry(Particle("minecraft:enchanted_hit"))
        addEntry(Particle("minecraft:enchant"))
        addEntry(Particle("minecraft:end_rod"))
        addEntry(Particle("minecraft:entity_effect"))
        addEntry(Particle("minecraft:explosion_emitter"))
        addEntry(Particle("minecraft:explosion"))
        addEntry(Particle("minecraft:gust"))
        addEntry(Particle("minecraft:small_gust"))
        addEntry(Particle("minecraft:gust_emitter_large"))
        addEntry(Particle("minecraft:gust_emitter_small"))
        addEntry(Particle("minecraft:sonic_boom"))
        addEntry(Particle("minecraft:falling_dust"))
        addEntry(Particle("minecraft:firework"))
        addEntry(Particle("minecraft:fishing"))
        addEntry(Particle("minecraft:flame"))
        addEntry(Particle("minecraft:infested"))
        addEntry(Particle("minecraft:cherry_leaves"))
        addEntry(Particle("minecraft:sculk_soul"))
        addEntry(Particle("minecraft:sculk_charge"))
        addEntry(Particle("minecraft:sculk_charge_pop"))
        addEntry(Particle("minecraft:soul_fire_flame"))
        addEntry(Particle("minecraft:soul"))
        addEntry(Particle("minecraft:flash"))
        addEntry(Particle("minecraft:happy_villager"))
        addEntry(Particle("minecraft:composter"))
        addEntry(Particle("minecraft:heart"))
        addEntry(Particle("minecraft:instant_effect"))
        addEntry(Particle("minecraft:item"))
        addEntry(Particle("minecraft:vibration"))
        addEntry(Particle("minecraft:trail"))
        addEntry(Particle("minecraft:item_slime"))
        addEntry(Particle("minecraft:item_cobweb"))
        addEntry(Particle("minecraft:item_snowball"))
        addEntry(Particle("minecraft:large_smoke"))
        addEntry(Particle("minecraft:lava"))
        addEntry(Particle("minecraft:mycelium"))
        addEntry(Particle("minecraft:note"))
        addEntry(Particle("minecraft:poof"))
        addEntry(Particle("minecraft:portal"))
        addEntry(Particle("minecraft:rain"))
        addEntry(Particle("minecraft:smoke"))
        addEntry(Particle("minecraft:white_smoke"))
        addEntry(Particle("minecraft:sneeze"))
        addEntry(Particle("minecraft:spit"))
        addEntry(Particle("minecraft:squid_ink"))
        addEntry(Particle("minecraft:sweep_attack"))
        addEntry(Particle("minecraft:totem_of_undying"))
        addEntry(Particle("minecraft:underwater"))
        addEntry(Particle("minecraft:splash"))
        addEntry(Particle("minecraft:witch"))
        addEntry(Particle("minecraft:bubble_pop"))
        addEntry(Particle("minecraft:current_down"))
        addEntry(Particle("minecraft:bubble_column_up"))
        addEntry(Particle("minecraft:nautilus"))
        addEntry(Particle("minecraft:dolphin"))
        addEntry(Particle("minecraft:campfire_cosy_smoke"))
        addEntry(Particle("minecraft:campfire_signal_smoke"))
        addEntry(Particle("minecraft:dripping_honey"))
        addEntry(Particle("minecraft:falling_honey"))
        addEntry(Particle("minecraft:landing_honey"))
        addEntry(Particle("minecraft:falling_nectar"))
        addEntry(Particle("minecraft:falling_spore_blossom"))
        addEntry(Particle("minecraft:ash"))
        addEntry(Particle("minecraft:crimson_spore"))
        addEntry(Particle("minecraft:warped_spore"))
        addEntry(Particle("minecraft:spore_blossom_air"))
        addEntry(Particle("minecraft:dripping_obsidian_tear"))
        addEntry(Particle("minecraft:falling_obsidian_tear"))
        addEntry(Particle("minecraft:landing_obsidian_tear"))
        addEntry(Particle("minecraft:reverse_portal"))
        addEntry(Particle("minecraft:white_ash"))
        addEntry(Particle("minecraft:small_flame"))
        addEntry(Particle("minecraft:snowflake"))
        addEntry(Particle("minecraft:dripping_dripstone_lava"))
        addEntry(Particle("minecraft:falling_dripstone_lava"))
        addEntry(Particle("minecraft:dripping_dripstone_water"))
        addEntry(Particle("minecraft:falling_dripstone_water"))
        addEntry(Particle("minecraft:glow_squid_ink"))
        addEntry(Particle("minecraft:glow"))
        addEntry(Particle("minecraft:wax_on"))
        addEntry(Particle("minecraft:wax_off"))
        addEntry(Particle("minecraft:electric_spark"))
        addEntry(Particle("minecraft:scrape"))
        addEntry(Particle("minecraft:shriek"))
        addEntry(Particle("minecraft:egg_crack"))
        addEntry(Particle("minecraft:dust_plume"))
        addEntry(Particle("minecraft:trial_spawner_detection"))
        addEntry(Particle("minecraft:trial_spawner_detection_ominous"))
        addEntry(Particle("minecraft:vault_connection"))
        addEntry(Particle("minecraft:dust_pillar"))
        addEntry(Particle("minecraft:ominous_spawning"))
        addEntry(Particle("minecraft:raid_omen"))
        addEntry(Particle("minecraft:trial_omen"))
        addEntry(Particle("minecraft:block_crumble"))
    }

    override fun get(identifier: String): Particle {
        return particles[identifier]
            ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): Particle? {
        return particles[identifier]
    }

    override fun getByProtocolId(id: Int): Particle {
        return particles.values.toList().getOrNull(id) ?: throw RegistryException(id, this.getMap().size)
    }

    override fun getMap(): Map<String, Particle> {
        return particles
    }
}

data class Particle(
    val identifier: String,
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return ParticleRegistry.protocolIds.getOrThrow(identifier)
    }

    override fun getNbt(): NBTCompound? = null
}