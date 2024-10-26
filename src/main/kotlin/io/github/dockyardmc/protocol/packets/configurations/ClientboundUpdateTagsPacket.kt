package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.netty.buffer.ByteBuf

@WikiVGEntry("Update Tags (configuration)")
@ClientboundPacketInfo(0x0D, ProtocolState.CONFIGURATION)
class ClientboundUpdateTagsPacket: ClientboundPacket() {

    val tags = mapOf<String, List<Tag>>(
        "minecraft:worldgen/biome" to listOf(
            Tag("minecraft:is_badlands", listOf()),
            Tag("minecraft:is_jungle", listOf()),
            Tag("minecraft:is_savanna", listOf())
        )
    )

    init {
        data.writeVarInt(tags.size)
        tags.forEach { tagArray ->
            data.writeString(tagArray.key)
            data.writeTagArray(tagArray.value)
        }

    }
}

data class Tag(
    val tagName: String,
    val tags: List<Int>
)

fun ByteBuf.writeTagArray(tagArray: List<Tag>) {
    this.writeVarInt(tagArray.size)
    tagArray.forEach { array ->
        this.writeString(array.tagName)
        this.writeVarInt(array.tags.size)
        array.tags.forEach { tag ->
            this.writeVarInt(tag)
        }
    }
}