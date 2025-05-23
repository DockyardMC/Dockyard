package io.github.dockyardmc.ui.new

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.ui.DrawableClickType
import io.github.dockyardmc.utils.debug

class CookieComponent : CompositeDrawable() {

    val cookieCount: Bindable<Int> = Bindable<Int>(1)

    override fun buildComponent() {

        withBindable(cookieCount) { _ ->
            withSlot(0) {
                withItemStack(ItemStack(Items.COOKIE).withMaxStackSize(256))
                withAmount(cookieCount.value)
                onClick { player, type ->
                    debug("<pink>CLICKED ($cookieCount)", true)
                    if (type != DrawableClickType.LEFT_CLICK) return@onClick

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