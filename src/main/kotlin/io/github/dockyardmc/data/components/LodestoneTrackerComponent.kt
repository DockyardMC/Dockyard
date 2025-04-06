package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.readOptional
import io.github.dockyardmc.protocol.types.WorldPosition
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf

class LodestoneTrackerComponent(val worldPosition: WorldPosition?, val tracked: Boolean) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeOptional(worldPosition, WorldPosition::write)
        buffer.writeBoolean(tracked)
    }

    companion object : NetworkReadable<LodestoneTrackerComponent> {
        override fun read(buffer: ByteBuf): LodestoneTrackerComponent {
            return LodestoneTrackerComponent(buffer.readOptional(WorldPosition::read), buffer.readBoolean())
        }
    }
}