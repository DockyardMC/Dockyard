package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class ProvidesTrimMaterialComponent(val materialIdentifier: String) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeBoolean(false)
        buffer.writeString(materialIdentifier)
    }

    companion object : NetworkReadable<ProvidesTrimMaterialComponent> {
        override fun read(buffer: ByteBuf): ProvidesTrimMaterialComponent {
            val direct = buffer.readBoolean()
            if (direct) throw UnsupportedOperationException("Cannot read direct trim material") //TODO Add support for direct trim material
            return ProvidesTrimMaterialComponent(buffer.readString())
        }
    }
}