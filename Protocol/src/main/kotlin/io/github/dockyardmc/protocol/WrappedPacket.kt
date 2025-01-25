package io.github.dockyardmc.protocol

data class WrappedPacket(val packet: Packet, val size: Int, val id: Int)
