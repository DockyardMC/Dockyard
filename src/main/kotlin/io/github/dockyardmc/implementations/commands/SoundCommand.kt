package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.SoundCategory
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.playSound

class SoundCommand {

    init {
        Commands.add("/playsound") {
            withPermission("dockyard.commands.playsound")
            withDescription("Plays a sound to player")

            addArgument("sound", SoundArgument())
            addArgument("category", EnumArgument(SoundCategory::class))
            addArgument("player", PlayerArgument())
            addOptionalArgument("volume", FloatArgument())
            addOptionalArgument("pitch", FloatArgument())

            execute {
                val player = getArgument<Player>("player")
                val sound = getArgument<Sound>("sound")
                val volume = getArgumentOrNull<Float>("volume") ?: 1f
                val pitch = getArgumentOrNull<Float>("pitch") ?: 1f
                val category = getEnumArgument<SoundCategory>("category")

                sound.category = category
                sound.pitch = pitch
                sound.volume = volume

                player.playSound(sound, player.location)
            }
        }
    }
}