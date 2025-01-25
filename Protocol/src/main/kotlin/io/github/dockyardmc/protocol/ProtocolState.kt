package io.github.dockyardmc.protocol

enum class ProtocolState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    CONFIGURATION,
    PLAY,
}