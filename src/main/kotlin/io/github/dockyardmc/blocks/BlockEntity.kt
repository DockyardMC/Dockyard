package io.github.dockyardmc.blocks

import io.github.dockyardmc.registry.registries.RegistryBlock
import org.jglrxavpok.hephaistos.nbt.NBTCompound

data class BlockEntity(
    val positionIndex: Int,
    val block: RegistryBlock,
    val data: NBTCompound,
) {
    val blockEntityTypeId get() = block.blockEntityId!!
}