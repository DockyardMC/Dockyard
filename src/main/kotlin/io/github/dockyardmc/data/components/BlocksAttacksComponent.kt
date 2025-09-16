package io.github.dockyardmc.data.components

import io.github.dockyardmc.codec.RegistryCodec
import io.github.dockyardmc.codec.transcoder.CRC32CTranscoder
import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.data.StaticHash
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.registry.registries.DamageType
import io.github.dockyardmc.registry.registries.DamageTypeRegistry
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.registry.registries.EntityTypeRegistry
import io.github.dockyardmc.sounds.SoundEvent
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.codec.StructCodec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.netty.buffer.ByteBuf

data class BlocksAttacksComponent(
    val blocksDelaySeconds: Float,
    val disableCooldownScale: Float,
    val damageReductions: List<DamageReduction>,
    val itemDamageFunction: ItemDamageFunction,
    val bypassedBy: DamageType?,
    val blockSound: SoundEvent?,
    val disableSound: SoundEvent?
) : DataComponent() {

    companion object : NetworkReadable<BlocksAttacksComponent> {

        val CODEC = StructCodec.of(
            "blocks_delay_seconds", Codec.FLOAT.default(0f), BlocksAttacksComponent::blocksDelaySeconds,
            "disable_cooldown_scale", Codec.FLOAT.default(1f), BlocksAttacksComponent::disableCooldownScale,
            "damage_reductions", DamageReduction.CODEC.list().default(listOf(DamageReduction.DEFAULT)), BlocksAttacksComponent::damageReductions,
            "item_damage", ItemDamageFunction.CODEC.default(ItemDamageFunction.DEFAULT), BlocksAttacksComponent::itemDamageFunction,
            "bypassed_by", RegistryCodec.codec(DamageTypeRegistry).optional(), BlocksAttacksComponent::bypassedBy,
            "block_sound", SoundEvent.CODEC.optional(), BlocksAttacksComponent::blockSound,
            "disabled_sound", SoundEvent.CODEC.optional(), BlocksAttacksComponent::disableSound,
            ::BlocksAttacksComponent
        )

        val STREAM_CODEC = StreamCodec.of(
            StreamCodec.FLOAT, BlocksAttacksComponent::blocksDelaySeconds,
            StreamCodec.FLOAT, BlocksAttacksComponent::disableCooldownScale,
            DamageReduction.STREAM_CODEC.list(), BlocksAttacksComponent::damageReductions,
            ItemDamageFunction.STREAM_CODEC, BlocksAttacksComponent::itemDamageFunction,
            RegistryCodec.stream(DamageTypeRegistry).optional(), BlocksAttacksComponent::bypassedBy,
            SoundEvent.STREAM_CODEC.optional(), BlocksAttacksComponent::blockSound,
            SoundEvent.STREAM_CODEC.optional(), BlocksAttacksComponent::disableSound,
            ::BlocksAttacksComponent
        )

        override fun read(buffer: ByteBuf): BlocksAttacksComponent {
            return STREAM_CODEC.read(buffer)
        }
    }

    override fun write(buffer: ByteBuf) {
        STREAM_CODEC.write(buffer, this)
    }

    override fun hashStruct(): HashHolder {
        return StaticHash(CODEC.encode(CRC32CTranscoder, this))
    }

    data class ItemDamageFunction(val threshold: Float, val base: Float, val factor: Float) : NetworkWritable, DataComponentHashable {

        companion object : NetworkReadable<ItemDamageFunction> {

            val DEFAULT = ItemDamageFunction(1f, 0f, 1f)

            val STREAM_CODEC = StreamCodec.of(
                StreamCodec.FLOAT, ItemDamageFunction::threshold,
                StreamCodec.FLOAT, ItemDamageFunction::base,
                StreamCodec.FLOAT, ItemDamageFunction::factor,
                ::ItemDamageFunction
            )

            val CODEC = StructCodec.of(
                "threshold", Codec.FLOAT, ItemDamageFunction::threshold,
                "base", Codec.FLOAT, ItemDamageFunction::base,
                "factor", Codec.FLOAT, ItemDamageFunction::factor,
                ::ItemDamageFunction
            )

            override fun read(buffer: ByteBuf): ItemDamageFunction {
                return STREAM_CODEC.read(buffer)
            }
        }

        override fun write(buffer: ByteBuf) {
            STREAM_CODEC.write(buffer, this)
        }


        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                static("threshold", CRC32CHasher.ofFloat(threshold))
                static("base", CRC32CHasher.ofFloat(base))
                static("factor", CRC32CHasher.ofFloat(factor))
            }
        }
    }

    data class DamageReduction(
        val horizontalBlockingAngle: Float,
        val type: EntityType?,
        val base: Float,
        val factor: Float
    ) : NetworkWritable, DataComponentHashable {

        override fun write(buffer: ByteBuf) {
            STREAM_CODEC.write(buffer, this)
        }

        companion object : NetworkReadable<DamageReduction> {

            val DEFAULT = DamageReduction(90.0f, null, 0.0f, 1.0f)

            val CODEC = StructCodec.of(
                "horizontal_blocking_angle", Codec.FLOAT.default(DEFAULT.horizontalBlockingAngle), DamageReduction::horizontalBlockingAngle,
                "type", RegistryCodec.codec(EntityTypeRegistry).optional(), DamageReduction::type,
                "base", Codec.FLOAT, DamageReduction::base,
                "factor", Codec.FLOAT, DamageReduction::factor,
                ::DamageReduction
            )

            val STREAM_CODEC = StreamCodec.of(
                StreamCodec.FLOAT, DamageReduction::horizontalBlockingAngle,
                RegistryCodec.stream(EntityTypeRegistry).optional(), DamageReduction::type,
                StreamCodec.FLOAT, DamageReduction::base,
                StreamCodec.FLOAT, DamageReduction::factor,
                ::DamageReduction
            )

            override fun read(buffer: ByteBuf): DamageReduction {
                return STREAM_CODEC.read(buffer)
            }
        }

        override fun hashStruct(): HashHolder {
            return CRC32CHasher.of {
                default("horizontal_blocking_angle", DEFAULT.horizontalBlockingAngle, horizontalBlockingAngle, CRC32CHasher::ofFloat)
                optional("type", type?.identifier, CRC32CHasher::ofString)
                static("base", CRC32CHasher.ofFloat(base))
                static("factor", CRC32CHasher.ofFloat(factor))
            }
        }
    }
}