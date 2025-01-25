package io.github.dockyardmc.annotations

import io.github.dockyardmc.protocol.packets.ProtocolState

annotation class ClientboundPacketInfo(val id: Int, val state: ProtocolState)
