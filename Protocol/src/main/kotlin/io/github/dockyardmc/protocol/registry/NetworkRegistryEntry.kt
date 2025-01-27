package io.github.dockyardmc.protocol.registry

import org.jglrxavpok.hephaistos.nbt.NBTCompound

class NetworkRegistryEntry(val nbtCompound: NBTCompound?, val identifierC: String): RegistryEntry() {

    override fun getIdentifier(): String {
        return identifierC
    }

    override fun getNbt(): NBTCompound? {
        return nbtCompound
    }
}