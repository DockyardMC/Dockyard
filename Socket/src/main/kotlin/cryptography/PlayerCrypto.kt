package io.github.dockyardmc.socket.cryptography

import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.SecretKey

class PlayerCrypto(
    val publicKey: PublicKey,
    val privateKey: PrivateKey,
    val verifyToken: ByteArray,
    var sharedSecret: SecretKey? = null,
    var isConnectionEncrypted: Boolean = false,
    var playerSession: PlayerSession? = null
)