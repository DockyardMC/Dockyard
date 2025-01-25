package io.github.dockyardmc.socket

object ChannelHandlers {
    const val RAW_PACKET_ENCODER = "raw-packet-encoder"
    const val PACKET_COMPRESSION_ENCODER = "packet-compression-encoder"
    const val PACKET_LENGTH_ENCODER = "packet-length-encoder"
    const val PACKET_DECRYPTOR = "packet-decryptor"

    const val RAW_PACKET_DECODER = "raw-packet-decoder"
    const val PACKET_COMPRESSION_DECODER = "compression-decoder"
    const val PACKET_LENGTH_DECODER = "packet-length-decoder"
    const val PACKET_ENCRYPTOR = "packet-encryptor"

    const val PLAYER_NETWORK_MANAGER = "player-network-manager"
}