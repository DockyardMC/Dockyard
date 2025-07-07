package io.github.dockyardmc.effects

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readRegistryEntry
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.github.dockyardmc.scheduler.runnables.ticks
import io.netty.buffer.ByteBuf
import kotlin.time.Duration

data class AppliedPotionEffect(
    var effect: PotionEffect,
    val settings: AppliedPotionEffectSettings,
    var startTime: Long? = null,
) : DataComponentHashable, NetworkWritable {

    override fun write(buffer: ByteBuf) {
        effect.write(buffer)
        settings.write(buffer)
    }

    companion object : NetworkReadable<AppliedPotionEffect> {
        override fun read(buffer: ByteBuf): AppliedPotionEffect {
            return AppliedPotionEffect(buffer.readRegistryEntry(PotionEffectRegistry), AppliedPotionEffectSettings.read(buffer))
        }
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            static("id", CRC32CHasher.ofRegistryEntry(effect))
            inline(settings, AppliedPotionEffectSettings::hashStruct)
            //start field is only for server-side use
        }
    }
}

data class AppliedPotionEffectSettings(
    val amplifier: Int,
    val duration: Duration,
    val isAmbient: Boolean,
    val showParticles: Boolean,
    val showIcon: Boolean,
    val hiddenEffect: AppliedPotionEffectSettings? = null
) : DataComponentHashable {
    companion object {

        fun read(buffer: ByteBuf): AppliedPotionEffectSettings {
            val amplifier: Int = buffer.readVarInt()
            val duration: Int = buffer.readVarInt()
            val isAmbient: Boolean = buffer.readBoolean()
            val showParticles: Boolean = buffer.readBoolean()
            val showIcon: Boolean = buffer.readBoolean()
            var hiddenEffect: AppliedPotionEffectSettings? = null

            if (buffer.readBoolean()) {
                hiddenEffect = read(buffer)
            }
            return AppliedPotionEffectSettings(amplifier, duration.ticks(), isAmbient, showParticles, showIcon, hiddenEffect)
        }
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            default("amplifier", 0, amplifier.toByte(), CRC32CHasher::ofByte)
            default("duration", 0, duration.inWholeMinecraftTicks, CRC32CHasher::ofInt)
            default("ambient", false, isAmbient, CRC32CHasher::ofBoolean)
            default("show_particles", true, showParticles, CRC32CHasher::ofBoolean)
            static("show_icon", CRC32CHasher.ofBoolean(showIcon))
            optionalStruct("hidden_effect", hiddenEffect, AppliedPotionEffectSettings::hashStruct)
        }
    }

    fun write(buffer: ByteBuf) {
        buffer.writeVarInt(amplifier)
        buffer.writeVarInt(duration.inWholeMinecraftTicks)
        buffer.writeBoolean(isAmbient)
        buffer.writeBoolean(showIcon)
        buffer.writeBoolean(showIcon)
        buffer.writeOptional(hiddenEffect, AppliedPotionEffectSettings::write)
    }
}