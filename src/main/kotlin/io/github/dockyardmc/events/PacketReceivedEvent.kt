package io.github.dockyardmc.events

import io.github.dockyardmc.protocol.packets.ServerboundPacket

class PacketReceivedEvent(
    val packet: ServerboundPacket
): Event