package io.github.dockyardmc.ui.new


class CookieClickerScreen : Screen() {

    override fun buildComponent() {
//        withRows(3)
        withComposite(22, CookieComponent())
    }


    override fun dispose() {

    }
}