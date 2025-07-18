package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.*
import io.github.dockyardmc.protocol.types.predicate.BlockTypeFilter
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

class ToolComponent(val rules: List<Rule>, val defaultMiningSpeed: Float, val damagePerBlock: Int, val canDestroyBlocksInCreative: Boolean) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(rules, Rule::write)
        buffer.writeFloat(defaultMiningSpeed)
        buffer.writeVarInt(damagePerBlock)
        buffer.writeBoolean(canDestroyBlocksInCreative)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            structList("rules", rules, Rule::hashStruct)
            default("default_mining_speed", DEFAULT_MINING_SPEED, defaultMiningSpeed, CRC32CHasher::ofFloat)
            default("damage_per_block", DEFAULT_DAMAGE_PER_BLOCK, damagePerBlock, CRC32CHasher::ofInt)
            default("can_destroy_blocks_in_creative", DEFAULT_CAN_DESTROY_BLOCKS_IN_CREATIVE, canDestroyBlocksInCreative, CRC32CHasher::ofBoolean)
        }
    }

    companion object : NetworkReadable<ToolComponent> {
        const val DEFAULT_MINING_SPEED = 1f
        const val DEFAULT_DAMAGE_PER_BLOCK = 1
        const val DEFAULT_CAN_DESTROY_BLOCKS_IN_CREATIVE = true

        override fun read(buffer: ByteBuf): ToolComponent {
            return ToolComponent(
                buffer.readList(Rule::read),
                buffer.readFloat(),
                buffer.readVarInt(),
                buffer.readBoolean()
            )
        }
    }

    data class Rule(val blocks: BlockTypeFilter, val speed: Float? = null, val correctForDrops: Boolean? = null) : NetworkWritable, DataComponentHashable {

        override fun write(buffer: ByteBuf) {
            blocks.write(buffer)
            buffer.writeOptional(speed, ByteBuf::writeFloat)
            buffer.writeOptional(correctForDrops, ByteBuf::writeBoolean)
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("blocks", blocks.hashStruct().getHashed())
                optional("speed", speed, CRC32CHasher::ofFloat)
                optional("correct_for_drops", correctForDrops, CRC32CHasher::ofBoolean)
            }
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