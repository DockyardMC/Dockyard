package io.github.dockyardmc.ui.new

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.registry.Items

class CookieComponent : CompositeDrawable() {

    val cookieCount: Bindable<Int> = Bindable<Int>(0)

    override fun buildComponent() {

        withSlot(5) {
            withItem(Items.COOKIE)
            withAmount(1)
            onClick { player, type ->
                cookieCount.value++
            }
        }
    }

    override fun onRender() {

    }

    override fun dispose() {
        cookieCount.dispose()
    }

}