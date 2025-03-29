package io.github.dockyardmc.protocol.types.predicate

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.readMap
import io.github.dockyardmc.protocol.types.writeMap
import io.github.dockyardmc.world.block.Block
import io.netty.buffer.ByteBuf
import java.util.function.Predicate

class PropertiesPredicate(val properties: Map<String, ValuePredicate>): Predicate<Block>, NetworkWritable {

    override fun test(block: Block): Boolean {
        properties.entries.forEach { entry ->
            val value = block.blockStates[entry.key]
            if(!entry.value.test(value)) return false
        }
        return true
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeMap<String, ValuePredicate>(properties, ByteBuf::writeString, ValuePredicate::write)
    }

    companion object: NetworkReadable<PropertiesPredicate> {
        override fun read(buffer: ByteBuf): PropertiesPredicate {
            return PropertiesPredicate(buffer.readMap<String, ValuePredicate>(ByteBuf::readString, ValuePredicate::read))
        }
    }

}



