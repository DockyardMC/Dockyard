package io.github.dockyardmc.protocol.registry

interface Registry {

    val identifier: String

    operator fun get(identifier: String): RegistryEntry

    fun getOrNull(identifier: String): RegistryEntry?

    fun getByProtocolId(id: Int): RegistryEntry

    fun getMap(): Map<String, RegistryEntry>

    fun getMaxProtocolId(): Int
}
