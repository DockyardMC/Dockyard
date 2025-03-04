package io.github.dockyardmc.protocol.cryptography

import java.time.Instant
import java.util.*

class PlayerSession(
    val sessionId: UUID,
    val expiry: Instant,
    val publicKey: ByteArray,
    val keySignature: ByteArray
)

