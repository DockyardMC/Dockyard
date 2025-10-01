package io.github.dockyard.tests.packets

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.protocol.packets.configurations.serverbound.ServerboundConfigurationCustomClickActionPacket
import io.github.dockyardmc.protocol.packets.registry.ServerPacketRegistry
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.functions
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class PacketsReadableTest {

    companion object {
        val IGNORED_PACKETS: List<KClass<*>> = listOf(
            ServerboundConfigurationCustomClickActionPacket::class
        )
    }

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
            if (IGNORED_PACKETS.contains(packetClass)) return@forEach

            log("Testing ${packetClass.simpleName}", LogType.DEBUG)
            assertNotNull(packetClass.companionObject)
            assertNotNull(packetClass.companionObject!!.functions.find { it.name == "read" })
        }
    }
}