package io.github.dockyard.tests.meta

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.player.EntityPose
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MetaTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }


    @Test
    fun testMetadata() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        assertEquals(EntityPose.STANDING, player.pose.value)




    }
}