package io.github.dockyardmc.protocol.encryption

import io.github.dockyardmc.player.PlayerConnectionEncryption
import javax.crypto.Cipher

object EncryptionUtil {

    fun getDecryptionCipherInstance(playerConnectionEncryption: PlayerConnectionEncryption): Cipher {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, playerConnectionEncryption.sharedSecret)
        return cipher
    }

    fun getEncryptionCipherInstance(playerConnectionEncryption: PlayerConnectionEncryption): Cipher {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, playerConnectionEncryption.sharedSecret)
        return cipher
    }
}