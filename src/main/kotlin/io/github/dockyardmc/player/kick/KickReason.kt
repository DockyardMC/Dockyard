package io.github.dockyardmc.player.kick

import io.github.dockyardmc.extentions.component
import io.github.dockyardmc.scroll.Component

enum class KickReason(val description: String) {
    FAILED_KEEP_ALIVE("You have been timed out"),
    FAILED_ENCRYPTION("You have sent invalid RSA public/private key"),
    FAILED_SESSION("Your session has expired (try restarting your game)"),
    INVALID_PLAYER_DATA("You have sent invalid player data, try again"),
}

fun getSystemKickMessage(kickReason: KickReason): Component {
    return getSystemKickMessage(kickReason.description, kickReason.name)
}

fun getSystemKickMessage(kickReason: String, enum: String? = null): Component {
    val message = buildString {
        appendLine("<red>Disconnected")
        appendLine()
        appendLine("<gray>$kickReason")
        enum?.let {
            appendLine("<dark_gray>err: $enum")
        }
    }
    return message.component()
}