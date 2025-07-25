package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryManager
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.registry.registries.tags.TagRegistry
import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import net.kyori.adventure.nbt.BinaryTag

class ClientboundUpdateTagsPacket(val registries: List<TagRegistry>) : ClientboundPacket() {

    init {
        buffer.writeVarInt(registries.size)
        registries.forEach { registry ->
            buffer.writeString(registry.identifier)
            buffer.writeList<Tag>(registry.getEntries().keyToValue().values.toList()) { buffer: ByteBuf, tag -> tag.write(buffer) }
        }
    }
}


@Serializable
data class Tag(
    val identifier: String,
    val tags: Set<String>,
    val registryIdentifier: String,
) : NetworkWritable, RegistryEntry {

    override fun getNbt(): BinaryTag? = null
    override fun getProtocolId(): Int = -1

    override fun getEntryIdentifier(): String {
        return identifier
    }

    operator fun contains(identifier: String): Boolean {
        return tags.contains(identifier)
    }

    override fun toString(): String {
        return "#$identifier"
    }

    override fun write(buffer: ByteBuf) {
        val registry = RegistryManager.getFromIdentifier<Registry<*>>(registryIdentifier)
        buffer.writeString(identifier)
        val intTags = tags.map { tag ->
            val entry = registry[tag]
            if (registry is BlockRegistry) (entry as RegistryBlock).getLegacyProtocolId() else entry.getProtocolId()
        }
        buffer.writeList(intTags, ByteBuf::writeVarInt)
    }
}
