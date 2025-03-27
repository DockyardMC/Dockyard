package io.github.dockyard.tests.advancements

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.advancement.AdvancementDisplay
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scroll.extensions.toComponent
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AdvancementDisplayTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testFlags() {
        val display = AdvancementDisplay(
            "Get wood".toComponent(),
            "Get OAK wood".toComponent(),
            Items.OAK_LOG.toItemStack(),
            background = null,
            showToast = true,
            isHidden = false
        )

        assertEquals(AdvancementDisplay.SHOW_TOAST, display.getFlags())

    }
}