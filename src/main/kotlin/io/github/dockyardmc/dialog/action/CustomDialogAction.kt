package io.github.dockyardmc.dialog.action

import io.github.dockyardmc.registry.DialogActionTypes
import io.github.dockyardmc.registry.registries.DialogActionType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

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