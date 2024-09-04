package io.github.dockyardmc.plugins.bundled.commands

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.SoundCategory
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.playSound

class SoundCommand {

    init {
        Commands.add("/playsound") {
            withPermission("dockyard.commands.playsound")
            withDescription("Plays sounds")

            addArgument("player", PlayerArgument())
            addArgument("sound", SoundArgument())
            addOptionalArgument("volume", FloatArgument())
            addOptionalArgument("volume", FloatArgument())
            addOptionalArgument("pitch", FloatArgument())
            addOptionalArgument("category", EnumArgument(SoundCategory::class))

            execute {
                val player = getArgument<Player>("player")
                val sound = getArgument<Sound>("sound")
                val volume = getArgumentOrNull<Float>("volume") ?: 0.5f
                val pitch = getArgumentOrNull<Float>("pitch") ?: 1f
                val category = getEnumArgumentOrNull<SoundCategory>("category") ?: SoundCategory.MASTER

                sound.category = category
                sound.pitch = pitch
                sound.volume = volume

                player.playSound(sound, player.location)
            }
        }
    }
}