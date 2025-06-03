package io.github.dockyardmc.dialog.action

import io.github.dockyardmc.registry.DialogActionTypes
import io.github.dockyardmc.registry.registries.DialogActionType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

/**
 * Command template will be proccessed by client and then executed as a command
 *
 * @property template The template.
 *
 * Example: `say $(arg1)`.
 *
 * will replace the `$(arg1)` with the contents of the input with key `arg1`
 */
class CommandTemplateDialogAction(val template: String) : DialogAction() {
    override val type: DialogActionType = DialogActionTypes.DYNAMIC_RUN_COMMAND

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("template", template)
        }
    }
}