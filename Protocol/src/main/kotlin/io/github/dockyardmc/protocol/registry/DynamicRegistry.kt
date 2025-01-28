package io.github.dockyardmc.protocol.registry

import io.github.dockyardmc.protocol.packets.configuration.clientbound.ClientboundRegistryDataPacket
import kotlin.reflect.KClass

interface DynamicRegistry: Registry {
    fun getEntryClass(): KClass<out RegistryEntry>
    fun getCachedPacket(): ClientboundRegistryDataPacket
    fun updateCache()
    fun register()
}