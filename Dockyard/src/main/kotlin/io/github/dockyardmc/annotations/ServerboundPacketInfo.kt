package io.github.dockyardmc.annotations

import io.github.dockyardmc.protocol.packets.ProtocolState

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ServerboundPacketInfo(val id: Int, val state: ProtocolState)