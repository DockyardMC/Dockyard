package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.packets.configurations.clientbound.ClientboundRegistryDataPacket
import io.github.dockyardmc.registry.DynamicRegistry
import io.github.dockyardmc.registry.RegistryEntry

object ChatTypeRegistry : DynamicRegistry<ChatType>() {

    override val identifier: String = "minecraft:chat_type"

    override fun updateCache() {
        cachedPacket = ClientboundRegistryDataPacket(this)
    }
}

class ChatType : RegistryEntry {

    override fun getProtocolId(): Int {
        throw UnsupportedOperationException()
    }

    override fun getEntryIdentifier(): String {
        throw UnsupportedOperationException()
    }
}