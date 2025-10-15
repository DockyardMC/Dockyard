package io.github.dockyardmc.entity.handlers

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.Disposable

class EntityMetadataHandler(override val entity: Entity) : EntityHandler, Disposable {

    private val metadata: MutableMap<Metadata.MetadataDefinition<*>, Metadata.MetadataDefinition.Value<*>> = mutableMapOf()
    private val metadataLayers: MutableMap<Player, MutableMap<Metadata.MetadataDefinition<*>, Metadata.MetadataDefinition.Value<*>>> = mutableMapOf()

    fun getValues(): Map<Metadata.MetadataDefinition<*>, Metadata.MetadataDefinition.Value<*>> {
        return metadata.toMap()
    }

    fun getValuesFor(player: Player): Map<Metadata.MetadataDefinition<*>, Metadata.MetadataDefinition.Value<*>> {
        if (!metadataLayers.containsKey(player)) {
            metadataLayers[player] = mutableMapOf()
        }

        return (metadataLayers[player]!!).toMap()
    }

    fun <T> getForPlayer(player: Player, definition: Metadata.MetadataDefinition<T>): T {
        if (!metadataLayers.containsKey(player)) {
            metadataLayers[player] = mutableMapOf()
        }
        val map = metadataLayers[player]!!
        return getInternal<T>(definition, map)
    }

    fun <T> setForPlayer(player: Player, definition: Metadata.MetadataDefinition<T>, value: T) {
        if (!metadataLayers.containsKey(player)) {
            metadataLayers[player] = mutableMapOf()
        }

        val map = metadataLayers[player]!!
        val result = setInternal<T>(definition, value, map)
        metadataLayers[player] = map
        return result
    }

    operator fun <T> get(definition: Metadata.MetadataDefinitionEntry<T>): T {
        return getInternal(definition, metadata)
    }

    operator fun <T> set(definition: Metadata.MetadataDefinitionEntry<T>, value: T) {
        return setInternal(definition, value, metadata)
    }

    fun removeForPlayer(player: Player, definition: Metadata.MetadataDefinitionEntry<*>) {
        if (!metadataLayers.containsKey(player)) {
            metadataLayers[player] = mutableMapOf()
        }
        val map = metadataLayers[player]!!
        removeInternal(definition, map)
    }

    fun remove(definition: Metadata.MetadataDefinitionEntry<*>) {
        removeInternal(definition, metadata)
    }

    private fun removeInternal(definition: Metadata.MetadataDefinitionEntry<*>, map: MutableMap<Metadata.MetadataDefinition<*>, Metadata.MetadataDefinition.Value<*>>) {
        map.remove(definition)
    }


    @Suppress("UNCHECKED_CAST")
    private fun <T> getInternal(definition: Metadata.MetadataDefinitionEntry<T>, map: MutableMap<Metadata.MetadataDefinition<*>, Metadata.MetadataDefinition.Value<*>>): T {
        return when (definition) {
            is Metadata.MetadataDefinition<T> -> {
                map[definition]?.value as T? ?: definition.default
            }

            is Metadata.BitmaskFlagDefinition -> {
                val parentValue = map[definition.parent]?.value as Byte? ?: definition.parent.default
                val isSet = (parentValue.toInt() and definition.bitMask.toInt()) != 0
                isSet as T
            }
        }
    }

    fun <T> setInternal(definition: Metadata.MetadataDefinitionEntry<T>, value: T, map: MutableMap<Metadata.MetadataDefinition<*>, Metadata.MetadataDefinition.Value<*>>) {
        when (definition) {
            is Metadata.MetadataDefinition<T> -> {
                map[definition] = Metadata.MetadataDefinition.Value(definition, value)
            }

            is Metadata.BitmaskFlagDefinition -> {
                val parentValue = map[definition.parent]?.value as Byte? ?: definition.parent.default
                val newValue = if (value as Boolean) {
                    (parentValue.toInt() or definition.bitMask.toInt()).toByte()
                } else {
                    (parentValue.toInt() and definition.bitMask.toInt().inv()).toByte()
                }
                map[definition.parent] = Metadata.MetadataDefinition.Value(definition.parent, newValue)
            }
        }
    }

    override fun dispose() {
        metadataLayers.clear()
        metadata.clear()
    }

    fun handleBindables(
        hasNoGravity: Bindable<Boolean>,
        entityIsOnFire: Bindable<Boolean>,
        freezeTicks: Bindable<Int>,
        isGlowing: Bindable<Boolean>,
        isInvisible: Bindable<Boolean>,
        pose: Bindable<EntityPose>,
        isSilent: Bindable<Boolean>,
        customName: Bindable<String?>,
        customNameVisible: Bindable<Boolean>,
        stuckArrows: Bindable<Int>,
        stuckStingers: Bindable<Int>,
    ) {
        hasNoGravity.valueChanged { event ->
            set(Metadata.HAS_NO_GRAVITY, event.newValue)
        }

        entityIsOnFire.valueChanged { event ->
            set(Metadata.IS_ON_FIRE, event.newValue)
        }

        freezeTicks.valueChanged { event ->
            set(Metadata.TICKS_FROZEN, event.newValue)
        }

        isSilent.valueChanged { event ->
            set(Metadata.IS_SILENT, event.newValue)
        }

        customName.valueChanged { event ->
            val textComponent = if (event.newValue != null) event.newValue!!.toComponent() else null
            set(Metadata.CUSTOM_NAME, textComponent)
        }

        customNameVisible.valueChanged { event ->
            set(Metadata.CUSTOM_NAME_VISIBLE, event.newValue)
        }

        isGlowing.valueChanged { event ->
            set(Metadata.HAS_GLOWING_EFFECT, event.newValue)
        }

        isInvisible.valueChanged { event ->
            set(Metadata.IS_INVISIBLE, event.newValue)
        }

        pose.valueChanged { event ->
            set(Metadata.POSE, event.newValue)
        }

        stuckArrows.valueChanged { event ->
            set(Metadata.LivingEntity.NUMBER_OF_ARROWS, event.newValue)
        }

        stuckStingers.valueChanged { event ->
            set(Metadata.LivingEntity.NUMBER_OF_BEE_STINGERS, event.newValue)
        }
    }
}