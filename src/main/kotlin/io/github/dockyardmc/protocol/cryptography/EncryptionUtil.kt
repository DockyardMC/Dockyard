package io.github.dockyardmc.protocol.cryptography

import io.github.dockyardmc.player.PlayerCrypto
import java.security.KeyPairGenerator
import java.util.concurrent.ThreadLocalRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

object EncryptionUtil {

    const val TRANSFORMATION = "AES/CFB8/NoPadding"

    fun getDecryptionCipherInstance(playerCrypto: PlayerCrypto): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, playerCrypto.sharedSecret, IvParameterSpec(playerCrypto.sharedSecret!!.encoded))
        return cipher
    }

    fun getEncryptionCipherInstance(playerCrypto: PlayerCrypto): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, playerCrypto.sharedSecret, IvParameterSpec(playerCrypto.sharedSecret!!.encoded))
        return cipher
    }

    fun getNewPlayerCrypto(): PlayerCrypto {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(1024)
        val keyPair = generator.generateKeyPair()
        val privateKey = keyPair.private
        val publicKey = keyPair.public

        val verificationToken = ByteArray(4)
        ThreadLocalRandom.current().nextBytes(verificationToken)

        return PlayerCrypto(publicKey, privateKey, verificationToken)
    }
}