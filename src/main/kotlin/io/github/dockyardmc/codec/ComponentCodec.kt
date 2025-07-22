package io.github.dockyardmc.codec

import com.google.gson.JsonElement
import io.github.dockyardmc.extentions.readTextComponent
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Transcoder
import io.github.dockyardmc.tide.asObjectOrThrow
import io.github.dockyardmc.tide.getPrimitive
import io.netty.buffer.ByteBuf
import net.kyori.adventure.nbt.TagStringIO

object ComponentCodec : Codec<Component> {

    override fun writeNetwork(buffer: ByteBuf, value: Component) {
        buffer.writeTextComponent(value)
    }

    override fun readJson(json: JsonElement, field: String): Component {
        val nbt = json.getPrimitive<String>(field)
        return TagStringIO.get().asCompound(nbt).toComponent()
    }

    override fun readNetwork(buffer: ByteBuf): Component {
        return buffer.readTextComponent()
    }

    override fun <A> readTranscoded(transcoder: Transcoder<A>, format: A, field: String): Component {
        throw UnsupportedOperationException()
    }

    override fun <A> writeTranscoded(transcoder: Transcoder<A>, format: A, value: Component, field: String) {
        throw UnsupportedOperationException()
    }

    override fun writeJson(json: JsonElement, value: Component, field: String) {
        json.asObjectOrThrow().addProperty(field, TagStringIO.get().asString(value.toNBT()))
    }

}