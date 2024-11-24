package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.registry.RegistryException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

object SoundRegistry {

    private val map: MutableMap<Int, String> = mutableMapOf()
    private val reversed: MutableMap<String, Int> = mutableMapOf()

    private val protocolIdCounter = AtomicInteger()

    fun addEntry(entry: String) {
        val id = protocolIdCounter.getAndIncrement()
        map[id] = entry
        reversed[entry] = id
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<String>>(stream)
        list.forEach(::addEntry)
    }

    operator fun get(identifier: String): Int {
        return reversed[identifier] ?: throw RegistryException(identifier, map.size)
    }

    fun getOrNull(identifier: String): Int? {
        return reversed[identifier]
    }

    fun getByProtocolId(id: Int): String {
        return map[id] ?: throw RegistryException(id, map.size)
    }

    fun getByProtocolIdOrNull(id: Int): String? {
        return map[id]
    }

    fun getMap(): Map<Int, String> {
        return map
    }

    fun getReversed(): Map<String, Int> {
        return reversed
    }
}