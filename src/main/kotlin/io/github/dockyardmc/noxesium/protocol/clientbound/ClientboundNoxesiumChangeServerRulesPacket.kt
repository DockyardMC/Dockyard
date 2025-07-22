package io.github.dockyardmc.noxesium.protocol.clientbound

import com.google.gson.JsonElement
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Transcoder
import io.netty.buffer.ByteBuf

data class ClientboundNoxesiumChangeServerRulesPacket(
    val writers: Map<Int, (ByteBuf) -> Unit>
) : NoxesiumPacket {

    override fun getStreamCodec(): Codec<out NoxesiumPacket> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = object : Codec<ClientboundNoxesiumChangeServerRulesPacket> {

            override fun writeNetwork(buffer: ByteBuf, value: ClientboundNoxesiumChangeServerRulesPacket) {
                val values = value.writers.entries.toList()
                val indices = values.map { it.key }
                buffer.writeList(indices, ByteBuf::writeVarInt)
                values.forEach { entry ->
                    entry.value.invoke(buffer)
                }
            }

            override fun readJson(json: JsonElement, field: String): ClientboundNoxesiumChangeServerRulesPacket {
                throw UnsupportedOperationException()
            }

            override fun readNetwork(buffer: ByteBuf): ClientboundNoxesiumChangeServerRulesPacket {
                throw UnsupportedOperationException()
            }

            override fun <A> readTranscoded(transcoder: Transcoder<A>, format: A, field: String): ClientboundNoxesiumChangeServerRulesPacket {
                throw UnsupportedOperationException()
            }

            override fun <A> writeTranscoded(transcoder: Transcoder<A>, format: A, value: ClientboundNoxesiumChangeServerRulesPacket, field: String) {
                throw UnsupportedOperationException()
            }

            override fun writeJson(json: JsonElement, value: ClientboundNoxesiumChangeServerRulesPacket, field: String) {
                throw UnsupportedOperationException()
            }

        }
    }
}