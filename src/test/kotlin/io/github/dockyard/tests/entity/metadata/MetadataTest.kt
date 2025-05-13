package io.github.dockyard.tests.entity.metadata

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.entity.metadata.getEntityMetadataStateBuilder
import io.github.dockyardmc.player.EntityPose
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MetadataTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
//        val player = PlayerTestUtil.getOrCreateFakePlayer()
//        player.hasNoGravity.value = false
//        player.isOnFire.value = false
//        player.freezeTicks.value = 0
//        player.isGlowing.value = false
//        player.isInvisible.value = false
//        player.pose.value = EntityPose.STANDING
    }

    @Test
    fun testEntityMetadata() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        assertEquals(false, player.hasNoGravity.value)
        assertEquals(false, player.isOnFire.value)
        assertEquals(0, player.freezeTicks.value)
        assertEquals(false, player.isGlowing.value)
        assertEquals(false, player.isInvisible.value)
        assertEquals(EntityPose.STANDING, player.pose.value)

        player.hasNoGravity.value = true
        player.isOnFire.value = true
        player.freezeTicks.value = 10
        player.isGlowing.value = true
        player.isInvisible.value = true
        player.pose.value = EntityPose.SWIMMING

        log("${player.metadata.values}", LogType.FATAL)
        val stateMeta = getEntityMetadataStateBuilder(player.metadata[EntityMetadataType.STATE].value as Byte, player)

        assertEquals(true, player.metadata[EntityMetadataType.HAS_NO_GRAVITY].value)
        assertEquals(true, stateMeta.isOnFire)
        assertEquals(10, player.metadata[EntityMetadataType.FROZEN_TICKS].value)
        assertEquals(true, stateMeta.isGlowing)
        assertEquals(true, stateMeta.isInvisible)
        assertEquals(EntityPose.SWIMMING, player.metadata[EntityMetadataType.POSE].value)
    }
}