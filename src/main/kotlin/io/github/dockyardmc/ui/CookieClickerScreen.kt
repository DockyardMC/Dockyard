package io.github.dockyardmc.ui

import cz.lukynka.Bindable
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.randomFloat

class CookieClickerScreen: DrawableContainerScreen() {

    override var name: String = "<black><bold>Cookie Clicker"
    override var rows: Int = 6

    val cookies: Bindable<Int> = Bindable(0)

    init {
        slots[1, 2] = Items.STONE.toDrawable()

        cookies.valueChanged {
            slots[3, 3] = drawableItemStack {
                withItem(Items.COOKIE, it.newValue)
                onClick { player, clickType ->
                    player.playSound(Sounds.ENTITY_GENERIC_EAT, 1f, randomFloat(1.2f, 1.8f))
                }
            }
        }
    }
}

