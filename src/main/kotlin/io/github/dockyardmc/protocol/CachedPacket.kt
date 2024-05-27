package io.github.dockyardmc.protocol

import io.github.dockyardmc.protocol.packets.ClientboundPacket

data class CachedPacket(
    var isValid: Boolean,
    var packet: ClientboundPacket
)