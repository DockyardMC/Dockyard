package io.github.dockyardmc.particles.data

import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.registries.Particle

interface ParticleData : NetworkWritable {

    val parentParticle: Particle

    companion object {
        val REQUIRE_PARTICLE_DATA: List<Particle> = listOf(
            Particles.BLOCK,
            Particles.BLOCK_MARKER,
            Particles.BLOCK_CRUMBLE,
            Particles.DUST,
            Particles.DUST_COLOR_TRANSITION,
            Particles.DUST_PILLAR,
            Particles.FALLING_DUST,
            Particles.SCULK_CHARGE,
            Particles.ITEM,
            Particles.VIBRATION,
            Particles.SHRIEK,
            Particles.TRAIL,
        )
    }
}