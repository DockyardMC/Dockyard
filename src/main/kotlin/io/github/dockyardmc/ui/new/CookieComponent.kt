package io.github.dockyardmc.ui.new

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.debug

class CookieComponent : CompositeDrawable() {

    val cookieCount: Bindable<Int> = Bindable<Int>(1)

    init {
        debug("<yellow><bold>CREATED COOKIE COMPONENT", true)
    }

    override fun buildComponent() {

        withBindable(cookieCount) { event ->
            debug("<pink>bindable event (${event.newValue})", true)
            withSlot(0) {
                withItemStack(ItemStack(Items.COOKIE).withMaxStackSize(256))
                withAmount(cookieCount.value)
                onClick { player, type ->
                    cookieCount.value++
                }
            }
        }
    }

    override fun dispose() {
        cookieCount.dispose()
    }

}