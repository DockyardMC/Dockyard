package io.github.dockyardmc.ui.examples

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.ui.DrawableContainerScreen
import io.github.dockyardmc.ui.drawableItemStack
import io.github.dockyardmc.ui.toDrawable
import io.github.dockyardmc.maths.randomFloat

class ExampleCookieClickerScreen(player: Player): DrawableContainerScreen(player) {

    override var name: String = "<black><bold>Cookie Clicker"
    override var rows: Int = 5

    val cookies: Bindable<Int> = Bindable(1)

    override fun onOpen(player: Player) {
        player.playSound(Sounds.BLOCK_WOODEN_BUTTON_CLICK_ON)
        cookies.triggerUpdate()
    }

    override fun onClose(player: Player) {
        player.playSound(Sounds.BLOCK_WOODEN_BUTTON_CLICK_OFF)
    }

    init {
        fill(0 to 0, 8 to 4, Items.BLACK_STAINED_GLASS_PANE.toDrawable())

        cookies.valueChanged {
            slots[4, 2] = drawableItemStack {
                withItem {
                    withMaterial(Items.COOKIE)
                    withDisplayName("<orange><b><u><i>Cookie<reset> <gray>(Click)")
                    addLore(" ")
                    addLore("<gray>You currently have <aqua>${it.newValue} cookies<gray>!")
                    addLore(" ")
                    withMaxStackSize(255)
                    withEnchantmentGlint(true)
                }
                onClick { player, clickType ->
                    cookies.value++
                    player.playSound(Sounds.ENTITY_GENERIC_EAT, 1f, randomFloat(0.8f, 1.4f))
                }
            }
        }
    }
}