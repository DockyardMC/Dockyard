package io.github.dockyardmc.codec

import io.github.dockyardmc.scroll.extensions.toComponent
import net.kyori.adventure.nbt.CompoundBinaryTag


object ComponentCodecs {

    val STREAM = BinaryTagCodecs.STREAM.transform({ from -> from.toNBT() }, { to -> (to as CompoundBinaryTag).toComponent() })

//
//    object StringType : Codec<String> {
//
//        override fun writeNetwork(buffer: ByteBuf, value: String) {
//            ComponentType.writeNetwork(buffer, value.toComponent())
//        }
//
//        override fun readJson(json: JsonElement, field: String): String {
//            return ComponentType.readJson(json, field).toJson()
//        }
//
//        override fun readNetwork(buffer: ByteBuf): String {
//            throw UnsupportedOperationException()
//        }
//
//        override fun <A> readTranscoded(transcoder: Transcoder<A>, format: A, field: String): String {
//            throw UnsupportedOperationException()
//        }
//
//        override fun <A> writeTranscoded(transcoder: Transcoder<A>, format: A, value: String, field: String) {
//            ComponentType.writeTranscoded(transcoder, format, value.toComponent(), field)
//        }
//
//        override fun writeJson(json: JsonElement, value: String, field: String) {
//            ComponentType.writeJson(json, value.toComponent(), field)
//        }
//
//
//    }
//
//    object ComponentType : Codec<Component> {
//
//        override fun writeNetwork(buffer: ByteBuf, value: Component) {
//            buffer.writeTextComponent(value)
//        }
//
//        override fun readJson(json: JsonElement, field: String): Component {
//            val nbt = json.getPrimitive<String>(field)
//            return TagStringIO.get().asCompound(nbt).toComponent()
//        }
//
//        override fun readNetwork(buffer: ByteBuf): Component {
//            return buffer.readTextComponent()
//        }
//
//        override fun <A> readTranscoded(transcoder: Transcoder<A>, format: A, field: String): Component {
//            throw UnsupportedOperationException()
//        }
//
//        override fun <A> writeTranscoded(transcoder: Transcoder<A>, format: A, value: Component, field: String) {
//            throw UnsupportedOperationException()
//        }
//
//        override fun writeJson(json: JsonElement, value: Component, field: String) {
//            json.asObjectOrThrow().addProperty(field, TagStringIO.get().asString(value.toNBT()))
//        }
//
//    }
}

