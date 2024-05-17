package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.SwingAnimationHand

@EventDocumentation("when player swings their hand", false)
class PlayerSwingHandEvent(player: Player, hand: SwingAnimationHand): Event