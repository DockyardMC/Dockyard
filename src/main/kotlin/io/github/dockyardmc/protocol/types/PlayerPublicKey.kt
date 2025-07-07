package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.readByteArray
import io.github.dockyardmc.extentions.readInstant
import io.github.dockyardmc.extentions.writeByteArray
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.cryptography.EncryptionUtil
import io.netty.buffer.ByteBuf
import kotlinx.datetime.Instant
import java.security.PublicKey

data class PlayerPublicKey(val expiresAt: Instant, val publicKey: PublicKey, val signature: ByteArray) : NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeLong(expiresAt.toEpochMilliseconds())
        buffer.writeByteArray(publicKey.encoded)
        buffer.writeByteArray(signature)
    }

    companion object : NetworkReadable<PlayerPublicKey> {
        override fun read(buffer: ByteBuf): PlayerPublicKey {
            return PlayerPublicKey(buffer.readInstant(), EncryptionUtil.publicRSAKeyFrom(buffer.readByteArray()), buffer.readByteArray())
        }
    }
}