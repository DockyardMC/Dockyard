package io.github.dockyardmc.entity.handlers

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableMap
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.metadata.*
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.PersistentPlayer
import io.github.dockyardmc.scroll.extensions.toComponent

class EntityMetadataHandler(override val entity: Entity) : EntityHandler {

    private val metadata: MutableMap<EntityMetadataType, EntityMetadata> = mutableMapOf<EntityMetadataType, EntityMetadata>()

    fun getValues(): Map<EntityMetadataType, EntityMetadata> {
        return metadata.toMap()
    }

    fun <T> setNew(definition: Metadata.MetadataDefinitionEntry<T>, value: T) {
        when (definition) {
            is Metadata.MetadataDefinition<T> -> {}
            is Metadata.BitmaskFlagDefinition<T> -> {}
        }
    }

    operator fun set(key: EntityMetadataType, value: EntityMetadata) {
        require(key == value.type) { "Not matching type" }

        synchronized(metadata) {
            metadata[key] = value
            entity.sendMetadataPacketToViewers()
            entity.sendSelfMetadataIfPlayer()
        }
    }

    fun remove(key: EntityMetadataType) {
        synchronized(metadata) {
            metadata.remove(key)
            entity.sendMetadataPacketToViewers()
            entity.sendSelfMetadataIfPlayer()
        }
    }

    operator fun get(key: EntityMetadataType): EntityMetadata {
        return getOrNull(key) ?: throw NoSuchElementException("No entity metadata with type ${key.name} present")
    }

    fun getOrNull(key: EntityMetadataType): EntityMetadata? {
        return metadata[key]
    }

    fun handleBindables(
        hasNoGravity: Bindable<Boolean>,
        entityIsOnFire: Bindable<Boolean>,
        freezeTicks: Bindable<Int>,
        metadataLayers: BindableMap<PersistentPlayer, MutableMap<EntityMetadataType, EntityMetadata>>,
        isGlowing: Bindable<Boolean>,
        isInvisible: Bindable<Boolean>,
        pose: Bindable<EntityPose>,
        isSilent: Bindable<Boolean>,
        customName: Bindable<String?>,
        customNameVisible: Bindable<Boolean>,
        stuckArrows: Bindable<Int>,
    ) {
        hasNoGravity.valueChanged {
            val noGravityType = EntityMetadataType.HAS_NO_GRAVITY
            set(noGravityType, EntityMetadata(noGravityType, EntityMetaValue.BOOLEAN, it.newValue))
        }

        entityIsOnFire.valueChanged {
            val meta = getEntityMetadataState(entity) {
                isOnFire = it.newValue
            }
            set(EntityMetadataType.STATE, meta)
        }

        freezeTicks.valueChanged {
            val meta = EntityMetadata(EntityMetadataType.FROZEN_TICKS, EntityMetaValue.VAR_INT, it.newValue)
            set(EntityMetadataType.FROZEN_TICKS, meta)
        }

        isSilent.valueChanged {
            val meta = EntityMetadata(EntityMetadataType.SILENT, EntityMetaValue.BOOLEAN, it.newValue)
            set(EntityMetadataType.SILENT, meta)
        }

        customName.valueChanged {
            val textComponent = if (it.newValue != null) it.newValue!!.toComponent() else null
            val meta = EntityMetadata(EntityMetadataType.CUSTOM_NAME, EntityMetaValue.OPTIONAL_TEXT_COMPONENT, textComponent)
            set(EntityMetadataType.CUSTOM_NAME, meta)
        }

        customNameVisible.valueChanged {
            val meta = EntityMetadata(EntityMetadataType.IS_CUSTOM_NAME_VISIBLE, EntityMetaValue.BOOLEAN, it.newValue)
            set(EntityMetadataType.IS_CUSTOM_NAME_VISIBLE, meta)
        }

        isGlowing.valueChanged {
            set(EntityMetadataType.STATE, getEntityMetadataState(entity))
        }

        isInvisible.valueChanged {
            set(EntityMetadataType.STATE, getEntityMetadataState(entity))
        }

        pose.valueChanged { event ->
            set(EntityMetadataType.POSE, EntityMetadata(EntityMetadataType.POSE, EntityMetaValue.POSE, event.newValue))
        }
    }
}