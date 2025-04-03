package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

class RecipesComponent(val recipes: List<String>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(recipes, ByteBuf::writeString)
    }

    companion object : NetworkReadable<RecipesComponent> {
        override fun read(buffer: ByteBuf): RecipesComponent {
            return RecipesComponent(buffer.readList(ByteBuf::readString))
        }
    }
}