package io.github.dockyardmc.dialog.action

import io.github.dockyardmc.extentions.modify
import io.github.dockyardmc.registry.DialogActionTypes
import io.github.dockyardmc.registry.registries.DialogActionType
import net.kyori.adventure.nbt.CompoundBinaryTag

/**
 * Client will send a [io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundCustomClickActionPacket]
 * with the data defined by dialog's *inputs*
 *
 * @property id ID of the custom click action
 * @property additions will be added to payload NBT tag by client
 */
class CustomDialogAction(val id: String, val additions: CompoundBinaryTag?) : DialogAction() {
    override val type: DialogActionType = DialogActionTypes.DYNAMIC_CUSTOM

    override fun getNbt(): CompoundBinaryTag {
        return super.getNbt().modify {
            withString("id", id)
            additions?.let {
                withCompound("additions", it)
            }
        }
    }
}