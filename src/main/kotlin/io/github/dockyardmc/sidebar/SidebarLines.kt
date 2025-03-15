package io.github.dockyardmc.sidebar

import io.github.dockyardmc.player.Player

interface SidebarLine

class GlobalSidebarLine(var value: String): SidebarLine

class PersonalizedSidebarLine(var line: (Player) -> String): SidebarLine {
    fun getValue(player: Player): String = line(player)
}