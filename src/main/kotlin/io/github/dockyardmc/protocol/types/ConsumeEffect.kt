package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.sounds.CustomSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.utils.BiMap
import io.netty.buffer.ByteBuf
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

interface ConsumeEffect : NetworkWritable {

    companion object : NetworkReadable<ConsumeEffect> {
        val effects = BiMap<Int, KClass<out ConsumeEffect>>()
        private val protocolIdCounter = AtomicInteger()

        init {
            register(ApplyEffects::class)
            register(RemoveEffects::class)
            register(ClearAllEffects::class)
            register(TeleportRandomly::class)
            register(PlaySound::class)
        }

        override fun read(buffer: ByteBuf): ConsumeEffect {
            val id = buffer.readVarInt()
            val kclass = effects.getByKey(id)
            return kclass.read(buffer)
        }

        private fun register(kclass: KClass<out ConsumeEffect>) {
            effects.put(protocolIdCounter.getAndIncrement(), kclass)
        }
    }

    data class ApplyEffects(val effects: List<AppliedPotionEffect>, val probability: Float) : ConsumeEffect {

        init {
            if (probability < 0f || probability > 1f) throw IllegalArgumentException("Probability must be between 0f and 1f")
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(ConsumeEffect.effects.getByValue(this::class))
            buffer.writeList(effects, ByteBuf::writeAppliedPotionEffect)
            buffer.writeFloat(probability)
        }

        companion object : NetworkReadable<ApplyEffects> {
            const val ID = 0

            override fun read(buffer: ByteBuf): ApplyEffects {
                return ApplyEffects(buffer.readList(ByteBuf::readAppliedPotionEffect), buffer.readFloat())
            }
        }

    }

    data class RemoveEffects(val effects: List<PotionEffect>) : ConsumeEffect {

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(ConsumeEffect.effects.getByValue(this::class))
            buffer.writeList(effects, RegistryEntry::write)
        }

        companion object : NetworkReadable<RemoveEffects> {
            const val ID = 1

            override fun read(buffer: ByteBuf): RemoveEffects {
                return RemoveEffects(buffer.readList { b -> RegistryEntry.read<PotionEffect>(b, PotionEffectRegistry) })
            }
        }
    }

    class ClearAllEffects() : ConsumeEffect {

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(effects.getByValue(this::class))
        }

        companion object : NetworkReadable<ClearAllEffects> {
            const val ID = 3

            override fun read(buffer: ByteBuf): ClearAllEffects {
                return ClearAllEffects()
            }
        }
    }

    data class TeleportRandomly(val diameter: Float) : ConsumeEffect {

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(effects.getByValue(this::class))
            buffer.writeFloat(diameter)
        }

        companion object : NetworkReadable<TeleportRandomly> {
            const val DEFAULT_DIAMETER = 16.0f
            const val ID = 3

            override fun read(buffer: ByteBuf): TeleportRandomly {
                return TeleportRandomly(buffer.readFloat())
            }
        }
    }

    data class PlaySound(val sound: String) : ConsumeEffect {

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(effects.getByValue(this::class))
            CustomSoundEvent(sound).write(buffer)
        }

        companion object : NetworkReadable<PlaySound> {
            const val ID = 4

            override fun read(buffer: ByteBuf): PlaySound {
                return PlaySound(SoundEvent.read(buffer).identifier)
            }
        }
    }
}