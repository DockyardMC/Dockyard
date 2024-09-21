package io.github.dockyardmc.player

import io.github.dockyardmc.protocol.cryptography.PlayerSession
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.SecretKey

class PlayerCrypto(
    val publicKey: PublicKey? = null,
    val privateKey: PrivateKey? = null,
    val verifyToken: ByteArray? = null,
    var sharedSecret: SecretKey? = null,
    var isConnectionEncrypted: Boolean = false,
    var playerSession: PlayerSession? = null
)