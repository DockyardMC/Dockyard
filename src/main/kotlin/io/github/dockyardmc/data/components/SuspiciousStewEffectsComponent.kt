package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.github.dockyardmc.scheduler.runnables.ticks
import io.netty.buffer.ByteBuf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SuspiciousStewEffectsComponent(val effects: List<Effect>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(effects, Effect::write)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CRC32CHasher.ofList(effects.map { effect -> effect.hashStruct().getHashed() }))
    }

    data class Effect(val potionEffect: PotionEffect, val duration: Duration) : DataComponentHashable, NetworkWritable {

        companion object : NetworkReadable<Effect> {
            val DEFAULT_DURATION = 8.seconds

            override fun read(buffer: ByteBuf): Effect {
                return Effect(buffer.readRegistryEntry(PotionEffectRegistry), buffer.readVarInt().ticks)
            }
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("id", CRC32CHasher.ofRegistryEntry(potionEffect))
                default("duration", DEFAULT_DURATION.inWholeMinecraftTicks, duration.inWholeMinecraftTicks, CRC32CHasher::ofInt)
            }
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeRegistryEntry(potionEffect)
            buffer.writeVarInt(duration.inWholeMinecraftTicks)
        }
    }

    companion object : NetworkReadable<SuspiciousStewEffectsComponent> {
        override fun read(buffer: ByteBuf): SuspiciousStewEffectsComponent {
            return SuspiciousStewEffectsComponent(buffer.readList(Effect::read))
        }
    }
}