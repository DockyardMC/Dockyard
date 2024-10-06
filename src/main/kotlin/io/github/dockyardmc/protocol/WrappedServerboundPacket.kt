package io.github.dockyardmc.protocol

import io.github.dockyardmc.protocol.packets.ServerboundPacket

// wrapped because ByteToMessageDecoder can only pass on one object
data class WrappedServerboundPacket(val packet: ServerboundPacket, val size: Int, val id: Int)