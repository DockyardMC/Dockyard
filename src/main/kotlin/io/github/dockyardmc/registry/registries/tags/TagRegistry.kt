package io.github.dockyardmc.registry.registries.tags

import io.github.dockyardmc.protocol.packets.configurations.Tag
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryException
import java.util.concurrent.atomic.AtomicInteger

abstract class TagRegistry : DataDrivenRegistry, DynamicRegistry {

    val tags: MutableMap<String, Tag> = mutableMapOf()
    val protocolIds: MutableMap<String, Int> = mutableMapOf()
    protected val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdCounter.get()
    }

    override fun get(identifier: String): Tag {
        return tags[identifier] ?: throw RegistryException(identifier, tags.size)
    }

    override fun getOrNull(identifier: String): Tag? {
        return tags[identifier]
    }

    override fun getByProtocolId(id: Int): Tag {
        return tags.toList().getOrNull(id)?.second ?: throw RegistryException(identifier, this.getMap().size)
    }

    override fun getMap(): Map<String, Tag> {
        return tags
    }
}