package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.registry.RegistryException

object MinecraftVersionRegistry {

    val versions: MutableMap<Int, MinecraftVersion> = mutableMapOf()

    fun addEntry(entry: MinecraftVersion) {
        versions[entry.protocolId] = entry
    }

    init {
        addEntry(MinecraftVersion(773, "1.21.9"))
        addEntry(MinecraftVersion(772, "1.21.8"))
        addEntry(MinecraftVersion(771, "1.21.6"))
        addEntry(MinecraftVersion(0x40000100, "1.21.6 Release Candidate 1"))
        addEntry(MinecraftVersion(0x400000FE, "1.21.6 Pre-Release 3"))
        addEntry(MinecraftVersion(0x400000FC, "1.21.6 Pre-Release 1"))
        addEntry(MinecraftVersion(0x400000FA, "25w20a"))
        addEntry(MinecraftVersion(770, "1.21.5"))
        addEntry(MinecraftVersion(769, "1.21.4"))
        addEntry(MinecraftVersion(768, "1.21.3"))
        addEntry(MinecraftVersion(767, "1.21"))
        addEntry(MinecraftVersion(766, "1.20.6"))
        addEntry(MinecraftVersion(766, "1.20.5"))
        addEntry(MinecraftVersion(765, "1.20.4"))
        addEntry(MinecraftVersion(765, "1.20.3"))
        addEntry(MinecraftVersion(764, "1.20.2"))
        addEntry(MinecraftVersion(763, "1.20.1"))
        addEntry(MinecraftVersion(763, "1.20"))
        addEntry(MinecraftVersion(762, "1.19.4"))
        addEntry(MinecraftVersion(761, "1.19.3"))
        addEntry(MinecraftVersion(760, "1.19.2"))
        addEntry(MinecraftVersion(760, "1.19.1"))
        addEntry(MinecraftVersion(759, "1.19"))
        addEntry(MinecraftVersion(758, "1.18.2"))
        addEntry(MinecraftVersion(757, "1.18.1"))
        addEntry(MinecraftVersion(757, "1.18"))
        addEntry(MinecraftVersion(756, "1.17.1"))
        addEntry(MinecraftVersion(755, "1.17"))
        addEntry(MinecraftVersion(754, "1.16.5"))
        addEntry(MinecraftVersion(754, "1.16.4"))
        addEntry(MinecraftVersion(753, "1.16.3"))
        addEntry(MinecraftVersion(751, "1.16.2"))
        addEntry(MinecraftVersion(736, "1.16.1"))
        addEntry(MinecraftVersion(735, "1.16"))
        addEntry(MinecraftVersion(578, "1.15.2"))
        addEntry(MinecraftVersion(575, "1.15.1"))
        addEntry(MinecraftVersion(573, "1.15"))
        addEntry(MinecraftVersion(498, "1.14.4"))
        addEntry(MinecraftVersion(490, "1.14.3"))
        addEntry(MinecraftVersion(485, "1.14.2"))
        addEntry(MinecraftVersion(480, "1.14.1"))
        addEntry(MinecraftVersion(477, "1.14"))
        addEntry(MinecraftVersion(404, "1.13.2"))
        addEntry(MinecraftVersion(401, "1.13.1"))
        addEntry(MinecraftVersion(393, "1.13"))
        addEntry(MinecraftVersion(340, "1.12.2"))
        addEntry(MinecraftVersion(338, "1.12.1"))
        addEntry(MinecraftVersion(335, "1.12"))
        addEntry(MinecraftVersion(316, "1.11.2"))
        addEntry(MinecraftVersion(921, "1.11.1"))
        addEntry(MinecraftVersion(315, "1.11"))
        addEntry(MinecraftVersion(210, "1.10.2"))
        addEntry(MinecraftVersion(511, "1.10.1"))
        addEntry(MinecraftVersion(510, "1.10"))
        addEntry(MinecraftVersion(110, "1.9.4"))
        addEntry(MinecraftVersion(183, "1.9.3"))
        addEntry(MinecraftVersion(176, "1.9.2"))
        addEntry(MinecraftVersion(108, "1.9.1"))
        addEntry(MinecraftVersion(169, "1.9"))
        addEntry(MinecraftVersion(47, "1.8.9"))
    }

    operator fun get(protocolId: Int): MinecraftVersion {
        return versions[protocolId] ?: throw RegistryException(protocolId, versions.size)
    }

    fun getOrNull(protocolId: Int): MinecraftVersion? {
        return versions[protocolId]
    }
}

data class MinecraftVersion(val protocolId: Int, val versionName: String)
