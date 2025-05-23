package io.github.dockyardmc.ui.new


class CookieClickerScreen : Screen() {

    override fun buildComponent() {
        withComposite(22, CookieComponent())
        withComposite(0, CloseScreenComponent())
    }

    override fun dispose() {

    }
}