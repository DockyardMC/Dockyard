package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.registry.registries.JukeboxSong
import io.github.dockyardmc.registry.registries.JukeboxSongRegistry
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class JukeboxPlayableComponent(val jukeboxSong: JukeboxSong) : DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(jukeboxSong.getProtocolId())
    }

    companion object : NetworkReadable<JukeboxPlayableComponent> {
        override fun read(buffer: ByteBuf): JukeboxPlayableComponent {
            return JukeboxPlayableComponent(buffer.readVarInt().let { int -> JukeboxSongRegistry.getByProtocolId(int) })
        }
    }
}