package io.github.dockyardmc.ui.new


class CookieClickerScreen : Screen() {

    override fun buildComponent() {
        withRows(3)
        withComposite(0, CookieComponent())
    }

    override fun onRender() {

    }

    override fun dispose() {

    }
}