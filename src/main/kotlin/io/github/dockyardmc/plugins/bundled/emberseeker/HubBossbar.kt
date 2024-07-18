package io.github.dockyardmc.plugins.bundled.emberseeker

import io.github.dockyardmc.bossbar.Bossbar
import io.github.dockyardmc.bossbar.BossbarColor
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.player.add

class HubBossbar {
    val serverBar = Bossbar("<#1ffffb>ᴇᴍʙᴇʀ sᴇᴇᴋᴇʀ <dark_gray>| <gray>ᴛʜɪs ɪs ᴜɴᴅᴇʀ ᴅᴇᴠᴇʟᴏᴘᴍᴇɴᴛ ᴀɴᴅ ɴᴏᴛ ɪɴᴅɪᴄᴀᴛɪᴠᴇ ᴏғ ғɪɴᴀʟ ᴘʀᴏᴅᴜᴄᴛ", 0f, BossbarColor.WHITE)

    init {
        Events.on<PlayerJoinEvent> {
            val player = it.player
            serverBar.viewers.add(player)
        }
    }
}