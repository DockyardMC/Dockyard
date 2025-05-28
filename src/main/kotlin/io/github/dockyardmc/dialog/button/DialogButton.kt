package io.github.dockyardmc.dialog.button

import io.github.dockyardmc.dialog.action.DialogAction
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DialogButton(
    override val label: String,
    override val tooltip: String? = null,
    override val width: Int = 150,
    val action: DialogAction? = null,
) : AbstractDialogButton() {
    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            action?.let {
                put("action", it.getNbt())
            }
        }
    }
}
