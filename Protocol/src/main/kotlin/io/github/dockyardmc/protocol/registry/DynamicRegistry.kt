package io.github.dockyardmc.protocol.registry

import io.github.dockyardmc.protocol.packets.configuration.clientbound.ClientboundRegistryDataPacket

interface DynamicRegistry: Registry {
    fun getCachedPacket(): ClientboundRegistryDataPacket
    fun updateCache()
    fun register()
}