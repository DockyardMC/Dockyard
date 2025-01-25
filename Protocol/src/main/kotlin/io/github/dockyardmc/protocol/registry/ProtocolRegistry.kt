package io.github.dockyardmc.protocol.registry

class ProtocolRegistry(override val identifier: String, val entries: Map<String, RegistryEntry>): Registry {

    override fun get(identifier: String): RegistryEntry {
        return entries[identifier] ?: throw throw RegistryException(identifier, this.getMap().size)
    }

    override fun getOrNull(identifier: String): RegistryEntry? {
        return entries[identifier]
    }

    override fun getByProtocolId(id: Int): RegistryEntry {
        return entries.toList()[id].second
    }

    override fun getMap(): Map<String, RegistryEntry> {
        return entries
    }

    override fun getMaxProtocolId(): Int {
        return entries.size + 1
    }
}