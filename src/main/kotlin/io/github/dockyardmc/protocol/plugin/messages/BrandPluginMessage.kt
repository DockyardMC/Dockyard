package io.github.dockyardmc.protocol.plugin.messages

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.player.Player
import io.netty.buffer.ByteBuf

class BrandPluginMessage(val brand: String): PluginMessageHandler() {

    companion object {
        fun read(buf: ByteBuf): BrandPluginMessage {
            return BrandPluginMessage(buf.readString())
        }
    }

    override fun handle(player: Player) {
        log("Received client brand from $player: $brand", LogType.DEBUG)
        player.brand = brand
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeUtf(brand)
    }
}