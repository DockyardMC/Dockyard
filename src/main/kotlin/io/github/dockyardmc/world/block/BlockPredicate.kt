package io.github.dockyardmc.world.block

import io.github.dockyardmc.extentions.*
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

fun ByteBuf.writeBlockPredicate(predicate: BlockPredicate) {
    this.writeBoolean(predicate.hasBlocks)
    if (predicate.hasBlocks) {
        this.writeBlockSet(predicate.blocks!!)
    }
    this.writeBoolean(predicate.properties!!.isNotEmpty())
    if (predicate.properties.isNotEmpty()) {
        this.writeVarInt(predicate.properties.size)
        predicate.properties.forEach {
            this.writeBlockProperty(it)
        }
    }
    this.writeBoolean(predicate.nbt != null)
    if (predicate.nbt != null) {
        this.writeNBT(predicate.nbt)
    }
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

fun ByteBuf.writeBlockSet(blockSet: BlockSet) {
    this.writeVarInt(blockSet.type)
    if (blockSet.type == 0) {
        this.writeString(blockSet.tagName!!)
    } else {
        val size = blockSet.blockIds!!.size
        this.writeVarInt(size + 1)
        blockSet.blockIds.forEach {
            this.writeVarInt(it)
        }
    }
}

fun ByteBuf.readBlockProperty(): BlockPredicateProperty {
    val name = this.readString()
    val isExactMatch = this.readBoolean()

    val exactValue = if(isExactMatch) this.readString() else null
    val minValue = if(!isExactMatch) this.readString() else null
    val maxValue = if(!isExactMatch) this.readString() else null

    return BlockPredicateProperty(name, isExactMatch, exactValue, minValue, maxValue)
}

fun ByteBuf.writeBlockProperty(property: BlockPredicateProperty) {
    this.writeString(property.name)
    this.writeBoolean(property.isExactMatch)

    if (property.isExactMatch) {
        this.writeString(property.exactValue!!)
    } else {
        this.writeString(property.minValue!!)
        this.writeString(property.maxValue!!)
    }
}