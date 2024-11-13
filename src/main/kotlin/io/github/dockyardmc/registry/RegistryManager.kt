package io.github.dockyardmc.registry

import cz.lukynka.prettylog.log
import io.github.dockyardmc.registry.registries.*
import java.io.InputStream
import kotlin.reflect.KClass

object RegistryManager {

    val dataDrivenRegisterSources: Map<KClass<*>, String> = mapOf(
        EntityTypeRegistry::class to "registry/entity_type_registry.json.gz",
        BlockRegistry::class to "registry/block_registry.json.gz",
        BiomeRegistry::class to "registry/biome_registry.json.gz",
        ItemRegistry::class to "registry/item_registry.json.gz",
        SoundRegistry::class to "registry/sound_registry.json.gz"
    )

    val dynamicRegistries: MutableList<Registry> = mutableListOf()

    fun register(registry: Registry) {
        if(registry is DataDrivenRegistry) {
            registry.initialize(ClassLoader.getSystemResource(dataDrivenRegisterSources[registry::class]!!).openStream())
        }
        if (registry is DynamicRegistry) {
            registry.register()
            registry.updateCache()
        }

        dynamicRegistries.add(registry)
    }

    fun getStreamForClass(registry: KClass<*>): InputStream {
        log(dataDrivenRegisterSources.toString())
        return ClassLoader.getSystemResource(dataDrivenRegisterSources[registry::class]!!).openStream()
    }

    fun getStreamFromPath(path: String): InputStream {
        return ClassLoader.getSystemResource(path).openStream()
    }
}