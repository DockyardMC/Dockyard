package io.github.dockyard.tests.particles

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.particles.data.DustParticleData
import io.github.dockyardmc.particles.data.SculkChargeParticleData
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSendParticlePacket
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.world.WorldManager
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.BeforeTest
import kotlin.test.Test

class ParticleDataTest {

    private companion object {
        val ZERO = WorldManager.mainWorld.locationAt(0, 0, 0)
        val PARTICLE = Particles.DUST
    }

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    // Particle data is present and is valid for the particle type
    @Test
    fun testParticleDataPresentValid() {
        assertDoesNotThrow {
            ClientboundSendParticlePacket(
                location = ZERO,
                particle = PARTICLE,
                offset = Vector3f(0f),
                speed = 0f,
                count = 1,
                overrideLimiter = false,
                alwaysShow = true,
                particleData = DustParticleData("#ff0000", 6.9f)
            )
        }
    }

    // Particle data is not present, should throw
    @Test
    fun testParticleDataNotPresent() {
        assertThrows<IllegalArgumentException> {
            ClientboundSendParticlePacket(
                location = ZERO,
                particle = PARTICLE,
                offset = Vector3f(0f),
                speed = 0f,
                count = 1,
                overrideLimiter = false,
                alwaysShow = true,
                particleData = null
            )
        }
    }

    // Particle data is present but is for the wrong particle type, should throw
    @Test
    fun testParticleDataPresentInvalid() {
        assertThrows<IllegalArgumentException> {
            ClientboundSendParticlePacket(
                location = ZERO,
                particle = PARTICLE,
                offset = Vector3f(0f),
                speed = 0f,
                count = 1,
                overrideLimiter = false,
                alwaysShow = true,
                particleData = SculkChargeParticleData(3f)
            )
        }
    }
}