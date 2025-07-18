package io.github.dockyardmc.utils.kotlinx

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.serializers.JsonToComponentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ComponentToJsonSerializer : KSerializer<Component> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("component", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Component {
        val string = decoder.decodeString()
        return JsonToComponentSerializer.serialize(string)
    }

    override fun serialize(encoder: Encoder, value: Component) {
        throw UnsupportedOperationException()
    }

}