package io.github.dockyardmc.ui

import cz.lukynka.Bindable
import io.github.dockyardmc.item.EnchantmentGlintOverrideItemComponent
import io.github.dockyardmc.item.MaxStackSizeItemComponent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.randomFloat

class CookieClickerScreen(player: Player): DrawableContainerScreen(player) {

    override var name: String = "<black><bold>Cookie Clicker"
    override var rows: Int = 5

    val cookies: Bindable<Int> = Bindable(1)

    override fun onOpen(player: Player) {
        cookies.triggerUpdate()
        player.playSound(Sounds.BLOCK_WOODEN_BUTTON_CLICK_ON)
    }

    override fun onClose(player: Player) {
        player.playSound(Sounds.BLOCK_WOODEN_BUTTON_CLICK_OFF)
    }

    init {
        fill(0 to 0, 8 to 4, Items.BLACK_STAINED_GLASS_PANE.toDrawable())

        cookies.valueChanged {
            slots[4, 2] = drawableItemStack {
                withItem(Items.COOKIE, it.newValue)
                withName("<orange><b><u><i>Cookie<reset> <gray>(Click)")
                addLoreLine("")
                addLoreLine("<gray>You currently have <aqua>${it.newValue} cookies<gray>!")
                addLoreLine("")
                withComponent(MaxStackSizeItemComponent(255))
                withComponent(EnchantmentGlintOverrideItemComponent(true))
                onClick { player, clickType ->
                    cookies.value++
                    player.playSound(Sounds.ENTITY_GENERIC_EAT, 1f, randomFloat(0.8f, 1.4f))
                }
            }
        }
    }
}