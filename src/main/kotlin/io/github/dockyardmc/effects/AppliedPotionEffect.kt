package io.github.dockyardmc.effects

import io.github.dockyardmc.codec.DurationCodec
import io.github.dockyardmc.codec.RegistryCodec
import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import kotlin.time.Duration

data class AppliedPotionEffect(
    var effect: PotionEffect,
    val settings: Settings,
    var startTime: Long? = null,
) : DataComponentHashable {

    companion object {

        val STREAM_CODEC = StreamCodec.of(
            RegistryCodec.stream(PotionEffectRegistry), AppliedPotionEffect::effect,
            Settings.STREAM_CODEC, AppliedPotionEffect::settings,
            ::AppliedPotionEffect
        )

        val CODEC = StructCodec.of(
            "id", RegistryCodec.codec(PotionEffectRegistry), AppliedPotionEffect::effect,
            StructCodec.INLINE, Settings.CODEC, AppliedPotionEffect::settings,
            ::AppliedPotionEffect
        )
    }

    data class Settings(
        val amplifier: Int,
        val duration: Duration,
        val isAmbient: Boolean,
        val showParticles: Boolean,
        val showIcon: Boolean,
        val hiddenEffect: Settings? = null
    ) : DataComponentHashable {
        companion object {

            val STREAM_CODEC = StreamCodec.recursive { self ->
                StreamCodec.of(
                    StreamCodec.VAR_INT, Settings::amplifier,
                    DurationCodec.STREAM_CODEC_INT_TICKS, Settings::duration,
                    StreamCodec.BOOLEAN, Settings::isAmbient,
                    StreamCodec.BOOLEAN, Settings::showParticles,
                    StreamCodec.BOOLEAN, Settings::showIcon,
                    self.optional(), Settings::hiddenEffect,
                    ::Settings
                )
            }

            val CODEC = Codec.recursive { self ->
                StructCodec.of(
                    "amplifier", Codec.BYTE.transform<Int>({ from -> from.toInt() }, { to -> to.toByte() }).default(0), Settings::amplifier,
                    "duration", DurationCodec.CODEC_INT_TICKS.default(0.ticks), Settings::duration,
                    "ambient", Codec.BOOLEAN.default(false), Settings::isAmbient,
                    "show_particles", Codec.BOOLEAN.default(true), Settings::showParticles,
                    "show_icon", Codec.BOOLEAN, Settings::showIcon,
                    "hidden_effect", self.optional(), Settings::hiddenEffect,
                    ::Settings
                )
            }
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                default("amplifier", 0, amplifier.toByte(), CRC32CHasher::ofByte)
                default("duration", 0, duration.inWholeMinecraftTicks, CRC32CHasher::ofInt)
                default("ambient", false, isAmbient, CRC32CHasher::ofBoolean)
                default("show_particles", true, showParticles, CRC32CHasher::ofBoolean)
                static("show_icon", CRC32CHasher.ofBoolean(showIcon))
                optionalStruct("hidden_effect", hiddenEffect, Settings::hashStruct)
            }
        }
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("id", CRC32CHasher.ofRegistryEntry(effect))
            inline(settings, Settings::hashStruct)
        }
    }
}

