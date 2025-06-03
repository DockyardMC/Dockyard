package io.github.dockyardmc.dialog.action

import io.github.dockyardmc.registry.DialogActionTypes
import io.github.dockyardmc.registry.registries.DialogActionType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

/**
 * Client will send a [io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundCustomClickActionPacket]
 * with the data defined by dialog's *inputs*
 *
 * @property id ID of the custom click action
 * @property additions will be added to payload NBT tag by client
 */
class CustomDialogAction(val id: String, val additions: NBTCompound?) : DialogAction() {
    override val type: DialogActionType = DialogActionTypes.DYNAMIC_CUSTOM

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("id", id)
            additions?.let {
                put("additions", it)
            }
        }
    }
}