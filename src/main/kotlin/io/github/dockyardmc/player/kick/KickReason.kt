package io.github.dockyardmc.player.kick

import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent

enum class KickReason(val description: String) {
    FAILED_KEEP_ALIVE("You have been timed out"),
    FAILED_ENCRYPTION("You have sent invalid RSA public/private key"),
    FAILED_SESSION("Your session has expired (try restarting your game)"),
    INVALID_PLAYER_DATA("You have sent invalid player data, try again"),
    INCOMPATIBLE_VERSION("You are trying to join on unsupported version"),
    ERROR_WHILE_WRITING_PACKET("There was an error while writing packet")
}

fun getSystemKickMessage(kickReason: KickReason): Component = getSystemKickMessage(kickReason.description, kickReason.name)

fun getSystemKickMessage(kickReason: String, enum: String? = null): Component {
    val message = buildString {
        appendLine("<red><b>Disconnected")
        appendLine()
        appendLine("<gray>$kickReason")
        enum?.let {
            appendLine("<dark_gray>err: $enum")
        }
    }
    return message.toComponent()
}