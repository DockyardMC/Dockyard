package io.github.dockyardmc.provider

import kotlin.time.Duration

interface PlayerMessageProvider : Provider {

    fun sendTitle(title: String, subtitle: String = "", fadeIn: Duration, stay: Duration, fadeOut: Duration) {
        playerGetter.forEach { player -> player.sendTitle(title, subtitle, fadeIn, stay, fadeOut) }
    }

    fun sendActionBar(message: String) {
        playerGetter.forEach { player -> player.sendActionBar(message) }
    }

    fun sendMessage(message: String, isSystem: Boolean = false) {
        playerGetter.forEach { player -> player.sendMessage(message, isSystem) }
    }

}