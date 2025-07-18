package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.effects.AppliedPotionEffect
import io.github.dockyardmc.extentions.read
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.sounds.CustomSoundEvent
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.utils.MutableBiMap
import io.netty.buffer.ByteBuf
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

interface ConsumeEffect : NetworkWritable, DataComponentHashable {

    companion object : NetworkReadable<ConsumeEffect> {
        val effects = MutableBiMap<Int, KClass<out ConsumeEffect>>()
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
            require(probability in 0f..1f) { "Probability must be between 0f and 1f" }
        }

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(ConsumeEffect.effects.getByValue(this::class))
            buffer.writeList(effects, AppliedPotionEffect::write)
            buffer.writeFloat(probability)
        }

        companion object : NetworkReadable<ApplyEffects> {
            const val ID = 0

            override fun read(buffer: ByteBuf): ApplyEffects {
                return ApplyEffects(buffer.readList(AppliedPotionEffect::read), buffer.readFloat())
            }
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                structList("effects", effects, AppliedPotionEffect::hashStruct)
                default("probability", 1f, probability, CRC32CHasher::ofFloat)
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

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                list("effects", effects, CRC32CHasher::ofRegistryEntry)
            }
        }
    }

    class ClearAllEffects : ConsumeEffect {

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(effects.getByValue(this::class))
        }

        companion object : NetworkReadable<ClearAllEffects> {
            const val ID = 3

            override fun read(buffer: ByteBuf): ClearAllEffects {
                return ClearAllEffects()
            }
        }

        override fun hashStruct(): HashHolder {
            return StaticHash(CRC32CHasher.EMPTY_MAP)
        }
    }

    data class TeleportRandomly(val diameter: Float = DEFAULT_DIAMETER) : ConsumeEffect {

        override fun write(buffer: ByteBuf) {
            buffer.writeVarInt(effects.getByValue(this::class))
            buffer.writeFloat(diameter)
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                default("diameter", DEFAULT_DIAMETER, diameter, CRC32CHasher::ofFloat)
            }
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

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("sound", CustomSoundEvent(sound).hashStruct().getHashed())
            }
        }
    }
}