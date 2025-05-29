package io.github.dockyardmc.ui

class CookieClickerScreen : Screen() {

    override val rows: Int = 5
    override val name: String = "<black><bold>Cookie Clicker"

    override fun buildComponent() {
        withComposite(4, 2, CookieComponent())
    }
}