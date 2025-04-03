package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.DataComponentRegistry
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass

class TooltipDisplayComponent(val hideTooltip: Boolean, val hiddenComponents: List<KClass<out DataComponent>>): DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(hideTooltip)
        buffer.writeList(hiddenComponents.map { component -> DataComponentRegistry.dataComponentsByIdReversed.getValue(component) }, ByteBuf::writeVarInt)
    }

    companion object: NetworkReadable<TooltipDisplayComponent> {
        override fun read(buffer: ByteBuf): TooltipDisplayComponent {
            return TooltipDisplayComponent(
                buffer.readBoolean(),
                buffer.readList(ByteBuf::readVarInt).map { int -> DataComponentRegistry.dataComponentsById.getValue(int) }
            )
        }
    }
}