package io.github.dockyardmc.advancement

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeStringArray
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.utils.Viewable
import io.netty.buffer.ByteBuf

data class Advancement(
    val id: String,
    val parentId: String?,
    val display: AdvancementDisplay?,
    val requirements: List<List<String>>,
) : NetworkWritable, Viewable() {
    override var autoViewable: Boolean = false

    override fun write(buffer: ByteBuf) {
        buffer.writeOptional(parentId, ByteBuf::writeString)
        buffer.writeOptional(display) { buf, it -> it.write(buf); buf }
        buffer.writeVarInt(requirements.size)
        requirements.forEach(buffer::writeStringArray)

        buffer.writeBoolean(false) // thats 'Sends telemerty' field
    }

    override fun addViewer(player: Player) {
        player.advancementTracker.onAdvancementAdded(this)
    }

    override fun removeViewer(player: Player) {
        player.advancementTracker.onAdvancementRemoved(this)
    }
}

