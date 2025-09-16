package io.github.dockyardmc.protocol.plugin.messages

import io.github.dockyardmc.tide.stream.StreamCodec

data class BrandPluginMessage(val brand: String) : PluginMessage {

    override fun getStreamCodec(): StreamCodec<out PluginMessage> {
        return STREAM_CODEC
    }

    companion object {
        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.STRING, BrandPluginMessage::brand,
            ::BrandPluginMessage
        )
    }
//
//    override fun handle(player: Player) {
//        log("Received client brand from $player: $brand", LogType.DEBUG)
//        player.brand = brand
//    }
//
//    override fun write(buffer: ByteBuf) {
//        buffer.writeString(brand)
//    }
}