package io.github.dockyardmc.ui

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.playSound

class CookieComponent : CompositeDrawable() {

    val cookieCount: Bindable<Int> = Bindable<Int>(1)

    override fun buildComponent() {

        withBindable(cookieCount) { _ ->
            withSlot(0) {
                withItemStack(ItemStack(Items.COOKIE).withMaxStackSize(99))
                withAmount(cookieCount.value)
                withName("<orange><u>Cookie Clicker")
                withLore("", "<gray>You have: <yellow>${cookieCount} <gray>cookies!")
                withNoxesiumImmovable(true)
                onClick { player, _ ->
                    cookieCount.value++ // <- reactive, automatically re-renders anything in the `withBindable`
                    player.playSound(Sounds.ENTITY_GENERIC_EAT)
                }
            }
        }
    }

    override fun dispose() {
        cookieCount.dispose()
    }

}