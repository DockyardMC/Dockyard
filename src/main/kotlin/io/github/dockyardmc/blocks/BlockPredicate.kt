package io.github.dockyardmc.blocks

import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound

data class BlockPredicate(
    val hasBlocks: Boolean,
    val blocks: BlockSet?,
    val properties: MutableList<BlockPredicateProperty>?,
    val nbt: NBTCompound?
)

data class BlockSet(
    val type: Int,
    val tagName: String?,
    val blockIds: Collection<Int>?
)

data class BlockPredicateProperty(
    val name: String,
    val isExactMatch: Boolean,
    val exactValue: String?,
    val minValue: String?,
    val maxValue: String?
)

fun ByteBuf.readBlockPredicate(): BlockPredicate {
    val hasBlocks = this.readBoolean()
    var blockSet: BlockSet? = null
    if(hasBlocks) {
        blockSet = this.readBlockSet()
    }
    val hasProperties = this.readBoolean()
    val properties = mutableListOf<BlockPredicateProperty>()
    if(hasProperties) {
        val size = this.readVarInt()
        for (i in 0 until size) {
            properties.add(this.readBlockProperty())
        }
    }
    val hasNbt = this.readBoolean()
    val nbt = if(hasNbt) (this.readNBT() as NBTCompound) else null

    return BlockPredicate(
        hasBlocks,
        blockSet,
        properties,
        nbt
    )
}

fun ByteBuf.readBlockSet(): BlockSet {
    val type = this.readVarInt()
    var tagName: String? = null
    var blockIds: MutableList<Int>? = null
    if(type == 0) {
        tagName = this.readString()
    } else {
        val size = type - 1
        blockIds = mutableListOf<Int>()
        for (i in 0 until size) {
            blockIds.add(this.readVarInt())
        }
    }
    return BlockSet(type, tagName, blockIds)
}

fun ByteBuf.readBlockProperty(): BlockPredicateProperty {
    val name = this.readString()
    val isExactMatch = this.readBoolean()

    val exactValue = if(isExactMatch) this.readString() else null
    val minValue = if(!isExactMatch) this.readString() else null
    val maxValue = if(!isExactMatch) this.readString() else null

    return BlockPredicateProperty(name, isExactMatch, exactValue, minValue, maxValue)
}