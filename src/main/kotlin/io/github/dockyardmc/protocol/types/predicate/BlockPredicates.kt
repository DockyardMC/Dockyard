package io.github.dockyardmc.protocol.types.predicate

import io.github.dockyardmc.extentions.readList
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.world.block.Block
import io.netty.buffer.ByteBuf
import java.util.function.Predicate

class BlockPredicates(val predicates: List<BlockPredicate>) : Predicate<Block>, NetworkWritable {

    companion object : NetworkReadable<BlockPredicates> {
        val NEVER = BlockPredicates(listOf())

        override fun read(buffer: ByteBuf): BlockPredicates {
            return BlockPredicates(buffer.readList(BlockPredicate::read))
        }
    }

    override fun test(block: Block): Boolean {
        predicates.forEach { predicate ->
            if (predicate.test(block)) return true
        }
        return false
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeList(predicates, BlockPredicate::write)
    }
}