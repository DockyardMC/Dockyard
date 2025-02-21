package io.github.dockyardmc.registry.registries.tags

import io.github.dockyardmc.protocol.packets.configurations.ClientboundRegistryDataPacket
import io.github.dockyardmc.protocol.packets.configurations.Tag
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
import java.util.zip.GZIPInputStream

object ItemTagRegistry : TagRegistry() {

    override val identifier: String = "minecraft:item"

    @OptIn(ExperimentalSerializationApi::class)
    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<Tag>>(stream)
        list.forEach { tag ->
            addEntry(tag)
        }
    }

    override fun getCachedPacket(): ClientboundRegistryDataPacket {
        throw NotImplementedError()
    }

    override fun updateCache() {}

    override fun register() {}

    fun addEntry(entry: Tag) {
        protocolIds[entry.identifier] = protocolIdCounter.getAndIncrement()
        tags[entry.identifier] = entry
    }
}