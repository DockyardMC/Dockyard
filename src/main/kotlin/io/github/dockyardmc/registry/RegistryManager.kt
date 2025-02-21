package io.github.dockyardmc.registry

import cz.lukynka.prettylog.log
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.registry.registries.tags.*
import java.io.InputStream
import java.lang.IllegalStateException
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
    )

    val dynamicRegistries: MutableMap<String, Registry> = mutableMapOf()

    fun register(registry: Registry) {
        if(registry is DataDrivenRegistry) {
            val resource = ClassLoader.getSystemResource(dataDrivenRegisterSources[registry::class]) ?: throw IllegalStateException("No resource file path for registry ${registry.identifier}")
            registry.initialize(resource.openStream())
        }
        if (registry is DynamicRegistry) {
            registry.register()
            registry.updateCache()
        }

        if(registry !is TagRegistry) dynamicRegistries[registry.identifier] = registry
    }

    fun getStreamForClass(registry: KClass<*>): InputStream {
        log(dataDrivenRegisterSources.toString())
        return ClassLoader.getSystemResource(dataDrivenRegisterSources[registry::class]!!).openStream()
    }

    fun getStreamFromPath(path: String): InputStream {
        return ClassLoader.getSystemResource(path).openStream()
    }

    fun <T: Registry> getFromIdentifier(identifier: String): T {
        return (dynamicRegistries[identifier] ?: throw NoSuchElementException("Registry with identifier $identifier was not found!")) as T
    }
}