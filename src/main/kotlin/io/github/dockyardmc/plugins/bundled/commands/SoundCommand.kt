package io.github.dockyardmc.plugins.bundled.commands

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.SoundCategory
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.playSound

class SoundCommand {

    init {
        Commands.add("/playsound") {
            it.permission = "dockyard.commands.playsound"
            it.addArgument("player", PlayerArgument())
            it.addArgument("sound", SoundArgument())
            it.addOptionalArgument("volume", FloatArgument())
            it.addOptionalArgument("volume", FloatArgument())
            it.addOptionalArgument("pitch", FloatArgument())
            it.addOptionalArgument("category", EnumArgument(SoundCategory::class))

            it.execute { ctx ->
                val player = it.get<Player>("player")
                val sound = it.get<Sound>("sound")
                val volume = it.getOrNull<Float>("volume") ?: 0.5f
                val pitch = it.getOrNull<Float>("pitch") ?: 1f
                val category = it.getEnumOrNull<SoundCategory>("category") ?: SoundCategory.MASTER

                sound.category = category
                sound.pitch = pitch
                sound.volume = volume

                player.playSound(sound, player.location)
            }
        }
    }
}