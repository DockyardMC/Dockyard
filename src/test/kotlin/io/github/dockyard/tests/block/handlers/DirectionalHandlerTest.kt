package io.github.dockyard.tests.block.handlers

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.inventory.clearInventory
import kotlin.test.BeforeTest
import kotlin.test.Test

class DirectionalHandlerTest {

    @BeforeTest
    fun before() {
        TestServer.getOrSetupServer()
        PlayerTestUtil.getOrCreateFakePlayer().clearInventory()
        BlockHandlerTestUtil.reset()
    }

    @Test
    fun testPlaceOnSurface() {
    }
}