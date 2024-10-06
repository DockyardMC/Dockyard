package io.github.dockyardmc.protocol.cryptography

import io.github.dockyardmc.player.PlayerCrypto
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

object EncryptionUtil {

    fun getDecryptionCipherInstance(playerCrypto: PlayerCrypto): Cipher {
        val cipher = Cipher.getInstance("AES/CFB8/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, playerCrypto.sharedSecret, IvParameterSpec(playerCrypto.sharedSecret!!.encoded))
        return cipher
    }

    fun getEncryptionCipherInstance(playerCrypto: PlayerCrypto): Cipher {
        val cipher = Cipher.getInstance("AES/CFB8/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, playerCrypto.sharedSecret, IvParameterSpec(playerCrypto.sharedSecret!!.encoded))
        return cipher
    }
}