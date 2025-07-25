package io.github.dockyardmc.registry

import io.github.dockyardmc.profiler.profiler
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.registry.registries.tags.*
import java.io.InputStream
import kotlin.reflect.KClass

object RegistryManager {

    val dataDrivenRegisterSources: Map<KClass<*>, String> = mapOf(
        EntityTypeRegistry::class to "registry/entity_type_registry.json.gz",
        BlockRegistry::class to "registry/block_registry.json.gz",
        BiomeRegistry::class to "registry/biome_registry.json.gz",
        ItemRegistry::class to "registry/item_registry.json.gz",
        SoundRegistry::class to "registry/sound_registry.json.gz",
        FluidRegistry::class to "registry/fluid_registry.json.gz",
        ParticleRegistry::class to "registry/particle_registry.json.gz",
        BiomeTagRegistry::class to "registry/biome_tags.json.gz",
        BlockTagRegistry::class to "registry/block_tags.json.gz",
        EntityTypeTagRegistry::class to "registry/entity_type_tags.json.gz",
        FluidTagRegistry::class to "registry/fluid_tags.json.gz",
        ItemTagRegistry::class to "registry/item_tags.json.gz",
        AttributeRegistry::class to "registry/attribute_registry.json.gz",
        WolfVariantRegistry::class to "registry/wolf_variant.json.gz",
        WolfSoundVariantRegistry::class to "registry/wolf_sound_variant.json.gz",
        CatVariantRegistry::class to "registry/cat_variant.json.gz",
        CowVariantRegistry::class to "registry/cow_variant.json.gz",
        PigVariantRegistry::class to "registry/pig_variant.json.gz",
        FrogVariantRegistry::class to "registry/frog_variant.json.gz",
        ChickenVariantRegistry::class to "registry/chicken_variant.json.gz",
        PotionTypeRegistry::class to "registry/potion_type_registry.json.gz",
        BannerPatternRegistry::class to "registry/banner_pattern_registry.json.gz",
        DamageTypeRegistry::class to "registry/damage_type_registry.json.gz",
        JukeboxSongRegistry::class to "registry/jukebox_song_registry.json.gz",
        TrimMaterialRegistry::class to "registry/trim_material_registry.json.gz",
        TrimPatternRegistry::class to "registry/trim_pattern_registry.json.gz",
        PaintingVariantRegistry::class to "registry/painting_variant_registry.json.gz",
        PotionEffectRegistry::class to "registry/potion_effect_registry.json.gz",
    )

    val dynamicRegistries: MutableMap<String, Registry<*>> = mutableMapOf()
    val registries = mutableListOf<Registry<*>>()

    inline fun <reified T : RegistryEntry> register(registry: Registry<*>) {
        profiler("Load ${registry::class.simpleName}") {
            registries.add(registry)
            if (registry is DataDrivenRegistry<*>) {
                val resource = ClassLoader.getSystemResource(dataDrivenRegisterSources[registry::class]) ?: throw IllegalStateException("No resource file path for registry ${registry.identifier}")
                registry.initialize<T>(resource.openStream())
            }
            if (registry is DynamicRegistry) {
                registry.updateCache()
            }

            if (registry !is TagRegistry) dynamicRegistries[registry.identifier] = registry
        }
    }

    fun getStreamForClass(registry: KClass<*>): InputStream {
        return ClassLoader.getSystemResource(dataDrivenRegisterSources[registry::class]!!).openStream()
    }

    fun getStreamFromPath(path: String): InputStream {
        return ClassLoader.getSystemResource(path).openStream()
    }

    fun <T : Registry<*>> getFromIdentifier(identifier: String): T {
        return (dynamicRegistries[identifier] ?: throw NoSuchElementException("Registry with identifier $identifier was not found!")) as T
    }
}