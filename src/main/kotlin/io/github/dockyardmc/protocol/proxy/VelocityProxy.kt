package io.github.dockyardmc.protocol.proxy

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.toByteArraySafe
import io.netty.buffer.ByteBuf
import java.lang.RuntimeException
import java.security.Key
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object VelocityProxy {
    const val PLAYER_INFO_CHANNEL: String = "velocity:player_info"
    const val SUPPORTED_FORWARDING_VERSION = 1
    const val MAC_ALGORITHM = "HmacSHA256" //tim apple???

    var enabled: Boolean = false
        private set

    private var key: Key? = null

    fun enabled(secret: String) {
        enabled = true
        key = SecretKeySpec(secret.toByteArray(), MAC_ALGORITHM)
        log("Enabled Velocity Proxy support!", LogType.SUCCESS)
    }

    fun checkIntegrity(buffer: ByteBuf): Boolean {
        val signature = ByteArray(32)
        for (i in signature.indices) {
            signature[i] = buffer.readByte()
        }
        val index = buffer.readerIndex()
        val data = buffer.readBytes(buffer.readableBytes())
        buffer.readerIndex(index)
        try {
            val mac = Mac.getInstance(MAC_ALGORITHM)
            mac.init(key)
            val mySignature = mac.doFinal(data.toByteArraySafe())
            if (!MessageDigest.isEqual(signature, mySignature)) {
                return false
            }

        } catch (exception: Exception) {
            log("Error while checking if velocity modern forwarding is enabled", LogType.ERROR)
            log(exception)
            throw RuntimeException(exception)
        }
        val version = buffer.readVarInt()
        return version == SUPPORTED_FORWARDING_VERSION
    }
}

