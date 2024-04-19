package io.github.dockyardmc.protocol

import io.github.dockyardmc.DECRYPT
import io.github.dockyardmc.extentions.readByteArray
import io.github.dockyardmc.player.PlayerConnectionEncryption
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.Unpooled
import log
import javax.crypto.Cipher
import kotlin.reflect.full.declaredMemberProperties

object PacketDecryptor {

    @OptIn(ExperimentalStdlibApi::class)
    fun decrypt(packet: ServerboundPacket, playerConnectionEncryption: PlayerConnectionEncryption) {
        val clazz = packet::class

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, playerConnectionEncryption.privateKey)

        clazz.declaredMemberProperties.forEach {
            val name = it.name
            val type = it.returnType.toString().replace("kotlin.", "")
            val value = it.call(packet)
            log(" ")
            log("$name ($type) - $value", DECRYPT)

            val valueWithProperType = when (it.returnType) {
                String::class -> value as String
                Int::class -> value as Int
                ByteArray::class -> value as ByteArray
                else -> value
            }

            if(valueWithProperType is ByteArray) {
                val decrypted = cipher.doFinal(valueWithProperType)
                log(decrypted.toHexString(), DECRYPT)
            }
        }
    }

}