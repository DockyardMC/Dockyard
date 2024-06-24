package io.github.dockyardmc.utils

import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundTitleTimesPacket
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent

const val DEFAULT_FADE_IN = 10
const val DEFAULT_STAY = 70
const val DEFAULT_FADE_OUT = 20

class Title(
    val title: Component?,
    val subtitle: Component?,
    val fadeIn: Int = DEFAULT_FADE_IN,
    val stay: Int = DEFAULT_STAY,
    val fadeOut: Int = DEFAULT_FADE_OUT
) {
    fun buildTimesPacket(): ClientboundTitleTimesPacket {
        return ClientboundTitleTimesPacket(
            fadeIn,
            stay,
            fadeOut
        )
    }
}