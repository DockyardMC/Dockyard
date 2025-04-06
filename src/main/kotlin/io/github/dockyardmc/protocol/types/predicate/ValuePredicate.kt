package io.github.dockyardmc.protocol.types.predicate

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf
import java.util.function.Predicate

interface ValuePredicate : Predicate<String?>, NetworkWritable {

    companion object: NetworkReadable<ValuePredicate> {
        override fun read(buffer: ByteBuf): ValuePredicate {
            val isExact = buffer.readBoolean()
            return if(isExact) Exact.read(buffer) else Range.read(buffer)
        }
    }

    data class Exact(val value: String?) : ValuePredicate {

        override fun test(prop: String?): Boolean {
            return prop != null && prop == value
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeBoolean(true)
            buffer.writeString(value!!)
        }

        companion object: NetworkReadable<Exact> {
            override fun read(buffer: ByteBuf): Exact {
                return Exact(buffer.readString())
            }
        }
    }

    data class Range(val min: String?, val max: String?) : ValuePredicate {

        override fun test(prop: String?): Boolean {
            if (prop == null || (min == null && max == null)) return false
            try {
                // try to match ints
                val value = prop.toInt()
                return (min == null || value >= min.toInt()) && (max == null || value < max.toInt())
            } catch (ex: NumberFormatException) {
                // not an integer, compare strings
                return (min == null || prop >= min) && (max == null || prop < max)
            }
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeBoolean(false)
            buffer.writeOptional<String>(min, ByteBuf::writeString)
            buffer.writeOptional<String>(max, ByteBuf::writeString)
        }

        companion object: NetworkReadable<Range> {
            override fun read(buffer: ByteBuf): Range {
                val min = buffer.readOptional<String>(ByteBuf::readString)
                val max = buffer.readOptional<String>(ByteBuf::readString)
                return Range(min, max)
            }
        }
    }
}