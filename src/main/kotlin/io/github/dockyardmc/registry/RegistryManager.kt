package io.github.dockyardmc.registry

import cz.lukynka.prettylog.log
import io.github.dockyardmc.registry.registries.BiomeRegistry
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.registry.registries.EntityTypeRegistry
import kotlin.reflect.KClass

object RegistryManager {

    val dataDrivenRegisterSources: Map<KClass<*>, String> = mapOf(
        EntityTypeRegistry::class to "registry/entity_type_registry.json.gz",
        BlockRegistry::class to "registry/block_registry.json.gz",
        BiomeRegistry::class to "registry/biome_registry.json.gz"
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
}