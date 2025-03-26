package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.advancement.Advancement
import io.github.dockyardmc.extentions.write
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeStringArray
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf

/**
 * @param reset Whether to reset/clear the current advancements.
 * @param advancementsAdd map of identifiers to [Advancement] to be added
 * @param advancementsRemove array of identifiers to be removed
 * @param progress map of identifier to progress to be updated
 */
class ClientboundUpdateAdvancementsPacket(
    val reset: Boolean,
    val advancementsAdd: Map<String, Advancement>,
    val advancementsRemove: List<String>,
    val progress: Map<String, Map<String, Long?>>
) : ClientboundPacket() {

    init {
        buffer.writeBoolean(reset)

        buffer.writeVarInt(advancementsAdd.size)
        advancementsAdd.forEach {
            buffer.writeString(it.key)
            buffer.write(it.value)
        }

        buffer.writeStringArray(advancementsRemove)

        buffer.writeVarInt(progress.size)
        progress.forEach { advId, advProgress ->
            buffer.writeString(advId)

            buffer.writeVarInt(advProgress.size)
            advProgress.forEach { criteria, timestamp ->
                buffer.writeString(criteria)
                buffer.writeOptional(timestamp, ByteBuf::writeLong)
            }
        }
    }

}