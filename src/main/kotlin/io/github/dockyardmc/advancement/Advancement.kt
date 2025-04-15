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
    val parent: Advancement?,
    val display: AdvancementDisplay?,
    val requirements: List<List<String>>,
) : NetworkWritable, Viewable() {

    private val innerChildren = mutableListOf<Advancement>()
    val children
        get() = synchronized(innerChildren) {
            innerChildren.toList()
        }

    override var autoViewable: Boolean = false

    init {
        parent?.innerChildren?.let {
            synchronized(it) {
                it.add(this)
            }
        }
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeOptional(parent?.id, ByteBuf::writeString)
        buffer.writeOptional(display) { buf, it -> it.write(buf); buf }
        buffer.writeVarInt(requirements.size)
        requirements.forEach(buffer::writeStringArray)

        buffer.writeBoolean(false) // that's 'Sends telemetry' field
    }

    /**
     * Adds the player as a viewer to this advancement
     * and all parents, all the way to root
     */
    override fun addViewer(player: Player) {
        // parents first
        this.parent?.addViewer(player)

        player.advancementTracker.onAdvancementAdded(this)
        viewers.add(player)
    }

    /**
     * Adds the player as a viewer
     * to this advancement and ALL children
     * recursively
     */
    fun addAll(player: Player) {
        addViewer(player)

        children.forEach { child ->
            child.addAll(player)
        }
    }

    /**
     * Removes the player-viewer from this advancement
     * and all children
     */
    override fun removeViewer(player: Player) {
        // children first
        synchronized(this.innerChildren) {
            this.innerChildren.forEach { child ->
                child.removeViewer(player)
            }
        }

        player.advancementTracker.onAdvancementRemoved(this)
        viewers.remove(player)
    }
}

