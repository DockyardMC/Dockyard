package io.github.dockyard.tests.advancements

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.advancement.Advancement
import io.github.dockyardmc.advancement.advancement
import io.github.dockyardmc.registry.Items
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AdvancementTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testFlags() {
        val adv = advancement("some_id") {
            withTitle("Get wood")
            withDescription("Get OAK wood")
            withIcon(Items.OAK_LOG)

            useToast(true)
            withHidden(false)
            withBackground(null)
        }

        assertEquals(Advancement.SHOW_TOAST, adv.getFlags())

    }
}