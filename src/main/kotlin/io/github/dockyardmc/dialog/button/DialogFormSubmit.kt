package io.github.dockyardmc.dialog.button

import io.github.dockyardmc.dialog.submit.DialogSubmitMethod
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DialogFormSubmit(
    val id: String,
    override val label: String,
    val submit: DialogSubmitMethod,
    override val tooltip: String? = null,
    override val width: Int = 150,
) : AbstractDialogButton() {
    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("id", id)
            put("on_submit", submit.getNbt())
        }
    }
}