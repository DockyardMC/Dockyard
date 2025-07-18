package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
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

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            optionalStruct("target", worldPosition, WorldPosition::hashStruct)
            default("tracked", true, tracked, CRC32CHasher::ofBoolean)
        }
    }

    companion object : NetworkReadable<LodestoneTrackerComponent> {
        override fun read(buffer: ByteBuf): LodestoneTrackerComponent {
            return LodestoneTrackerComponent(buffer.readOptional(WorldPosition::read), buffer.readBoolean())
        }
    }
}