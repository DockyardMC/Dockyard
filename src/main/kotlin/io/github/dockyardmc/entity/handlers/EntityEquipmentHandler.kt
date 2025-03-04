package io.github.dockyardmc.entity.handlers

import cz.lukynka.bindables.BindableMap
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.item.EquipmentSlot
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.PersistentPlayer
import io.github.dockyardmc.player.Player

class EntityEquipmentHandler(override val entity: Entity) : EntityHandler {

    fun handle(
        equipment: BindableMap<EquipmentSlot, ItemStack>,
        equipmentLayers: BindableMap<PersistentPlayer, Map<EquipmentSlot, ItemStack>>,
    ) {
        equipment.itemSet {
            if (entity !is Player) return@itemSet
            entity.inventory.unsafeUpdateEquipmentSlot(it.key, entity.heldSlotIndex.value, it.value)
        }

        equipment.mapUpdated {
            if (entity is Player) entity.sendEquipmentPacket(entity)
            entity.viewers.forEach { viewer -> entity.sendEquipmentPacket(viewer) }
        }

        equipmentLayers.itemSet {
            val player = it.key.toPlayer()
            if (player != null) entity.sendEquipmentPacket(player)
        }

        equipmentLayers.itemRemoved {
            val player = it.key.toPlayer()
            if (player != null) entity.sendEquipmentPacket(player)
        }
    }
}