package io.github.dockyardmc.protocol.encryption

import io.github.dockyardmc.player.PlayerConnectionEncryption
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

object EncryptionUtil {

    fun getDecryptionCipherInstance(playerConnectionEncryption: PlayerConnectionEncryption): Cipher {
        val cipher = Cipher.getInstance("AES/CFB8/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, playerConnectionEncryption.sharedSecret, IvParameterSpec(playerConnectionEncryption.sharedSecret!!.encoded))
        return cipher
    }

    fun getEncryptionCipherInstance(playerConnectionEncryption: PlayerConnectionEncryption): Cipher {
        val cipher = Cipher.getInstance("AES/CFB8/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, playerConnectionEncryption.sharedSecret, IvParameterSpec(playerConnectionEncryption.sharedSecret!!.encoded))
        return cipher
    }
}