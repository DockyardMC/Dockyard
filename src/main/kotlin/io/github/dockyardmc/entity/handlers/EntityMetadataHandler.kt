package io.github.dockyardmc.entity.handlers

import cz.lukynka.bindables.Bindable
import cz.lukynka.bindables.BindableMap
import io.github.dockyardmc.entity.*
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.player.PersistentPlayer
import io.github.dockyardmc.scroll.extensions.toComponent

class EntityMetadataHandler(override val entity: Entity) : EntityHandler {

    fun handle(
        hasNoGravity: Bindable<Boolean>,
        entityIsOnFire: Bindable<Boolean>,
        freezeTicks: Bindable<Int>,
        metadata: BindableMap<EntityMetadataType, EntityMetadata>,
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
            entity.metadata[noGravityType] = EntityMetadata(noGravityType, EntityMetaValue.BOOLEAN, it.newValue)
        }

        entityIsOnFire.valueChanged {
            val meta = getEntityMetadataState(entity) {
                isOnFire = it.newValue
            }
            entity.metadata[EntityMetadataType.STATE] = meta
        }

        freezeTicks.valueChanged {
            val meta = EntityMetadata(EntityMetadataType.FROZEN_TICKS, EntityMetaValue.VAR_INT, it.newValue)
            entity.metadata[EntityMetadataType.FROZEN_TICKS] = meta
        }

        isSilent.valueChanged {
            val meta = EntityMetadata(EntityMetadataType.SILENT, EntityMetaValue.BOOLEAN, it.newValue)
            entity.metadata[EntityMetadataType.SILENT] = meta
        }

        customName.valueChanged {
            val textComponent = if(it.newValue != null) it.newValue!!.toComponent() else null
            val meta = EntityMetadata(EntityMetadataType.CUSTOM_NAME, EntityMetaValue.OPTIONAL_TEXT_COMPONENT, textComponent)
            entity.metadata[EntityMetadataType.CUSTOM_NAME] = meta
        }

        customNameVisible.valueChanged {
            val meta = EntityMetadata(EntityMetadataType.IS_CUSTOM_NAME_VISIBLE, EntityMetaValue.BOOLEAN, it.newValue)
            entity.metadata[EntityMetadataType.IS_CUSTOM_NAME_VISIBLE] = meta
        }

        metadata.mapUpdated {
            entity.sendMetadataPacketToViewers()
            entity.sendSelfMetadataIfPlayer()
        }

        metadata.itemSet {
            entity.sendMetadataPacketToViewers()
            entity.sendSelfMetadataIfPlayer()
        }

        metadataLayers.itemSet {
            val player = it.key.toPlayer()
            if (player != null) entity.sendMetadataPacket(player)
        }

        metadataLayers.itemRemoved {
            val player = it.key.toPlayer()
            if (player != null) entity.sendMetadataPacket(player)
        }

        isGlowing.valueChanged {
            metadata[EntityMetadataType.STATE] = getEntityMetadataState(entity)
        }

        isInvisible.valueChanged {
            metadata[EntityMetadataType.STATE] = getEntityMetadataState(entity)
        }

        pose.valueChanged {
            metadata[EntityMetadataType.POSE] = EntityMetadata(EntityMetadataType.POSE, EntityMetaValue.POSE, it.newValue)
        }
    }
}