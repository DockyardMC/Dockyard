package io.github.dockyardmc.player

import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.SecretKey

class PlayerConnectionEncryption(val publicKey: PublicKey, val privateKey: PrivateKey, val verifyToken: ByteArray, var sharedSecret: SecretKey? = null, var isEncrypted: Boolean = false) {



}