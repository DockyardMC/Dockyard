package io.github.dockyardmc.noxesium.rules

import com.noxcrew.noxesium.api.protocol.rule.EntityRuleIndices
import com.noxcrew.noxesium.api.protocol.rule.ServerRuleIndices
import com.noxcrew.noxesium.api.qib.QibDefinition
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.protocol.types.writeMap
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.ByteBuf

object NoxesiumRules {

    data class RuleFunction<T : Any?>(val index: Int, val rule: (Int) -> NoxesiumServerRule<T>) {
        fun createRule(value: T): NoxesiumServerRule<T> {
            val rule = rule.invoke(index)
            rule.value = value
            return rule
        }
    }

    object Entity {
        val DISABLE_BUBBLES = register<Boolean>(EntityRuleIndices.DISABLE_BUBBLES, ::BooleanServerRule)
        val BEAM_COLOR = register<CustomColor?>(EntityRuleIndices.BEAM_COLOR, ::ColorServerRule)
        val QIB_BEHAVIOUR = register<String>(EntityRuleIndices.QIB_BEHAVIOR) { StringServerRule(it, "") }
        val INTERACTION_WIDTH_Z = register<Double>(EntityRuleIndices.QIB_WIDTH_Z, ::DoubleServerRule)
        val BEAM_FADE_COLOR = register<CustomColor?>(EntityRuleIndices.BEAM_COLOR_FADE, ::ColorServerRule)
        val CUSTOM_GLOW_COLOR = register<CustomColor?>(EntityRuleIndices.CUSTOM_GLOW_COLOR, ::ColorServerRule)

        fun <T : Any?> register(index: Int, rule: (Int) -> NoxesiumServerRule<T>): RuleFunction<T> {
            val function = RuleFunction(index, rule)
            return function
        }
    }

    object Server {
        val DISABLE_SPIN_ATTACK_COOLDOWN = register<Int>(ServerRuleIndices.DISABLE_SPIN_ATTACK_COLLISIONS, ::IntServerRule)
        val HELD_ITEM_NAME_OFFSET = register<Int>(ServerRuleIndices.HELD_ITEM_NAME_OFFSET, ::IntServerRule)
        val CAMERA_LOCKED = register<Boolean>(ServerRuleIndices.CAMERA_LOCKED, ::BooleanServerRule)
        val DISABLE_VANILLA_MUSIC = register<Boolean>(ServerRuleIndices.DISABLE_VANILLA_MUSIC, ::BooleanServerRule)
        val DISABLE_BOAT_COLLISION = register<Boolean>(ServerRuleIndices.DISABLE_BOAT_COLLISIONS, ::BooleanServerRule)
        val HAND_ITEM_OVERRIDE = register<ItemStack>(ServerRuleIndices.HAND_ITEM_OVERRIDE, ::ItemStackServerRule)
        val SHOW_MAP_IN_UI = register<Boolean>(ServerRuleIndices.SHOW_MAP_IN_UI, ::BooleanServerRule)
        val DISABLE_DEFFERED_CHUNK_UPDATES = register<Boolean>(ServerRuleIndices.DISABLE_DEFERRED_CHUNK_UPDATES, ::BooleanServerRule)
        val CUSTOM_CREATIVE_ITEMS = register<List<ItemStack>>(ServerRuleIndices.CUSTOM_CREATIVE_ITEMS, ::ItemStackListServerRule)
        val QIB_BEHAVIOURS = register<Map<String, QibDefinition>>(ServerRuleIndices.QIB_BEHAVIORS, ::QibBehaviourServerRule)
        val OVERRIDE_GRAPHICS_MODE = register<GraphicsType?>(ServerRuleIndices.OVERRIDE_GRAPHICS_MODE, ::OptionalEnumServerRule)
        val SMOOTHER_CLIENT_TRIDENT = register<Boolean>(ServerRuleIndices.ENABLE_SMOOTHER_CLIENT_TRIDENT, ::BooleanServerRule)
        val DISABLE_MAP_UI = register<Boolean>(ServerRuleIndices.DISABLE_MAP_UI, ::BooleanServerRule)
        val RIPTIDE_COYOTE_TIME = register<Int>(ServerRuleIndices.RIPTIDE_COYOTE_TIME, ::IntServerRule)
        val RIPTIDE_PRE_CHARGING = register<Boolean>(ServerRuleIndices.RIPTIDE_PRE_CHARGING, ::BooleanServerRule)
        val RESTRICT_DEBUG_OPTIONS = register<List<Int>>(ServerRuleIndices.RESTRICT_DEBUG_OPTIONS, ::IntListServerRule)

        fun <T : Any?> register(index: Int, rule: (Int) -> NoxesiumServerRule<T>): RuleFunction<T> {
            val function = RuleFunction(index, rule)
            return function
        }

        enum class GraphicsType {
            FAST,
            FANCY,
            FABULOUS,
        }
    }

    class IntServerRule(index: Int, defaultV: Int = 0) : NoxesiumServerRule<Int>(index, defaultV) {
        override fun write(value: Int, buffer: ByteBuf) {
            buffer.writeVarInt(value)
        }
    }

    class BooleanServerRule(index: Int, defaultV: Boolean = false) : NoxesiumServerRule<Boolean>(index, defaultV) {
        override fun write(value: Boolean, buffer: ByteBuf) {
            buffer.writeBoolean(value)
        }
    }

    class DoubleServerRule(index: Int, defaultV: Double = 0.0) : NoxesiumServerRule<Double>(index, defaultV) {
        override fun write(value: Double, buffer: ByteBuf) {
            buffer.writeDouble(value)
        }
    }

    class StringServerRule(index: Int, defaultV: String) : NoxesiumServerRule<String>(index, defaultV) {
        override fun write(value: String, buffer: ByteBuf) {
            buffer.writeString(value)
        }
    }

    class StringListServerRule(index: Int, defaultV: List<String> = listOf()) : NoxesiumServerRule<List<String>>(index, defaultV) {
        override fun write(value: List<String>, buffer: ByteBuf) {
            buffer.writeList(value, ByteBuf::writeString)
        }
    }

    class ItemStackServerRule(index: Int, defaultV: ItemStack = ItemStack.AIR) : NoxesiumServerRule<ItemStack>(index, defaultV) {
        override fun write(value: ItemStack, buffer: ByteBuf) {
            value.write(buffer)
        }
    }

    class ItemStackListServerRule(index: Int, defaultV: List<ItemStack> = listOf()) : NoxesiumServerRule<List<ItemStack>>(index, defaultV) {
        override fun write(value: List<ItemStack>, buffer: ByteBuf) {
            buffer.writeList(value, ItemStack::write)
        }
    }

    class ColorServerRule(index: Int, defaultV: CustomColor? = null) : NoxesiumServerRule<CustomColor?>(index, defaultV) {
        override fun write(value: CustomColor?, buffer: ByteBuf) {
            buffer.writeOptional(value?.asRGB(), ByteBuf::writeVarInt)
        }

        override fun toString(): String {
            return "ColorServerRule($value)"
        }
    }

    class OptionalEnumServerRule<T : Enum<T>>(index: Int, defaultV: T? = null) : NoxesiumServerRule<T?>(index, defaultV) {

        override fun write(value: T?, buffer: ByteBuf) {
            buffer.writeOptional(value?.ordinal, ByteBuf::writeVarInt)
        }
    }

    class QibBehaviourServerRule(index: Int, defaultV: Map<String, QibDefinition> = emptyMap()) : NoxesiumServerRule<Map<String, QibDefinition>>(index, defaultV) {
        override fun write(value: Map<String, QibDefinition>, buffer: ByteBuf) {
            buffer.writeMap(value.mapValues { map -> QibDefinition.QIB_GSON.toJson(map.value) }, ByteBuf::writeString, ByteBuf::writeString)
        }
    }

    class IntListServerRule(index: Int, defaultV: List<Int> = listOf()) : NoxesiumServerRule<List<Int>>(index, defaultV) {
        override fun write(value: List<Int>, buffer: ByteBuf) {
            buffer.writeList(value, ByteBuf::writeVarInt)
        }
    }
}