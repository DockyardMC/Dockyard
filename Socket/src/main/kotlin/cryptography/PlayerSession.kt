package io.github.dockyardmc.socket.cryptography

import java.time.Instant
import java.util.UUID

class PlayerSession(
    val sessionId: UUID,
    val expiry: Instant,
    val publicKey: ByteArray,
    val keySignature: ByteArray
)

