package io.github.dockyardmc.dialog.action

import io.github.dockyardmc.registry.DialogActionTypes
import io.github.dockyardmc.registry.registries.DialogActionType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class CommandTemplateDialogAction(val template: String) : DialogAction() {
    override val type: DialogActionType = DialogActionTypes.DYNAMIC_RUN_COMMAND

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("template", template)
        }
    }
}