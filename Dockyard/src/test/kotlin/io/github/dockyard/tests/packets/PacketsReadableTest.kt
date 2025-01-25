package io.github.dockyard.tests.packets

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.protocol.packets.registry.ServerPacketRegistry
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class PacketsReadableTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
    }

    @Test
    fun packetsHaveRead() {
        ServerPacketRegistry.getAll().forEach { packetClass ->
            log("Testing ${packetClass.simpleName}", LogType.DEBUG)
            assertNotNull(packetClass.companionObject)
            assertNotNull(packetClass.companionObject!!.declaredMemberFunctions.find { it.name == "read" })
        }
    }
}