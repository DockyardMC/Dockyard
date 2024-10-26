package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.writeItemStack
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Set Equipment")
@ClientboundPacketInfo(0x5B, ProtocolState.PLAY)
class ClientboundSetEntityEquipmentPacket(val entity: Entity, val equipment: EntityEquipment): ClientboundPacket() {

    init {
        data.writeVarInt(entity.entityId)
        val equipmentList = mutableMapOf<Int, ItemStack>()
        equipmentList[0] = equipment.mainHand
        equipmentList[1] = equipment.offHand
        equipmentList[2] = equipment.boots
        equipmentList[3] = equipment.leggings
        equipmentList[4] = equipment.chestplate
        equipmentList[5] = equipment.helmet
        equipmentList[6] = equipment.body

        var index = 0
        equipmentList.forEach {
            val last = index++ == equipmentList.size - 1
            var slotEnum = it.key.toByte()
            if (!last) slotEnum = (slotEnum.toInt() or 0x80).toByte()

            data.writeByte(slotEnum.toInt())
            data.writeItemStack(it.value)
        }
    }

}


fun getMergedEquipmentData(base: EntityEquipment, merge: EntityEquipmentLayer?): EntityEquipment {
    if(merge == null) return base
    val mainHand = merge.mainHand ?: base.mainHand
    val offHand = merge.offHand ?: base.offHand
    val boots = merge.boots ?: base.boots
    val leggings = merge.leggings ?: base.leggings
    val chestplate = merge.chestplate ?: base.chestplate
    val helmet = merge.helmet ?: base.helmet
    val body = merge.body ?: base.body

    return EntityEquipment(mainHand, offHand, boots, leggings, chestplate, helmet, body)
}

data class EntityEquipment(
    var mainHand: ItemStack = ItemStack.air,
    var offHand: ItemStack = ItemStack.air,
    var boots: ItemStack = ItemStack.air,
    var leggings: ItemStack = ItemStack.air,
    var chestplate: ItemStack = ItemStack.air,
    var helmet: ItemStack = ItemStack.air,
    val body: ItemStack = ItemStack.air,
)

data class EntityEquipmentLayer(
    val mainHand: ItemStack? = null,
    val offHand: ItemStack? = null,
    val boots: ItemStack? = null,
    val leggings: ItemStack? = null,
    val chestplate: ItemStack? = null,
    val helmet: ItemStack? = null,
    val body: ItemStack? = null,
)