package io.github.dockyardmc.protocol.registry

import org.jglrxavpok.hephaistos.nbt.NBTCompound

class ProtocolRegistryEntry(val nbt: NBTCompound?, val identifier: String): RegistryEntry() {

    override fun getIdentifier(): String {
        return identifier
    }

    override fun getNbt(): NBTCompound? {
        return nbt
    }
}