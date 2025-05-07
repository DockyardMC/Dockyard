package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.predicate.BlockTypeFilter
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class ToolComponent(val rules: List<Rule>, val defaultMiningSpeed: Float, val damagePerBlock: Int, val canDestroyBlocksInCreative: Boolean) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(rules, Rule::write)
        buffer.writeFloat(defaultMiningSpeed)
        buffer.writeVarInt(damagePerBlock)
        buffer.writeBoolean(canDestroyBlocksInCreative)
    }

    companion object : NetworkReadable<ToolComponent> {
        const val DEFAULT_MINING_SPEED = 1f
        const val DEFAULT_DAMAGE_PER_BLOCK = 1

        override fun read(buffer: ByteBuf): ToolComponent {
            return ToolComponent(
                buffer.readList(Rule::read),
                buffer.readFloat(),
                buffer.readVarInt(),
                buffer.readBoolean()
            )
        }
    }

    data class Rule(val blocks: BlockTypeFilter, val speed: Float? = null, val correctForDrops: Boolean? = null) : NetworkWritable {

        override fun write(buffer: ByteBuf) {
            blocks.write(buffer)
            buffer.writeOptional(speed, ByteBuf::writeFloat)
            buffer.writeOptional(correctForDrops, ByteBuf::writeBoolean)
        }

        companion object : NetworkReadable<Rule> {
            override fun read(buffer: ByteBuf): Rule {
                return Rule(
                    BlockTypeFilter.read(buffer),
                    buffer.readOptional(ByteBuf::readFloat),
                    buffer.readOptional(ByteBuf::readBoolean)
                )
            }
        }

    }
}