package io.github.dockyardmc.extentions

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.toComponent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun MutableList<Player>.sendMessage(message: String) {
    this.forEach { it.sendMessage(message) }
}

fun MutableList<Player>.sendMessage(message: Component) {
    this.forEach { it.sendMessage(message) }
}

fun MutableList<Player>.sendPacket(packet: ClientboundPacket) {
    this.forEach { it.sendPacket(packet) }
}

fun MutableList<Player>.sendActionBar(message: String) {
    this.forEach { it.sendActionBar(message) }
}

fun MutableList<Player>.sendActionBar(message: Component) {
    this.forEach { it.sendActionBar(message) }
}

fun MutableList<Player>.sendTitle(title: String, subtitle: String = "", fadeIn: Int = 10, stay: Int = 60, fadeOut: Int = 10) {
    this.forEach { it.sendTitle(title, subtitle, fadeIn, stay, fadeOut) }
}

fun MutableList<Player>.setTabHeader(header: String) {
    this.forEach { it.tabListHeader.value = header.toComponent() }
}

fun MutableList<Player>.setTabFooter(footer: String) {
    this.forEach { it.tabListFooter.value = footer.toComponent() }
}

fun <E> MutableList<E>.addIfNotPresent(target: E) {
    if(!this.contains(target)) this.add(target)
}

fun <E> MutableList<E>.removeIfPresent(target: E) {
    if(this.contains(target)) this.remove(target)
}