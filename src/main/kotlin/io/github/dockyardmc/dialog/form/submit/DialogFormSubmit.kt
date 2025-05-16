package io.github.dockyardmc.dialog.form.submit

import io.github.dockyardmc.dialog.button.AbstractDialogButton
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DialogFormSubmit(
    val id: String,
    label: String,
    val submit: DialogSubmitMethod,
    tooltip: String? = null,
    width: Int = 150
) : AbstractDialogButton(label, tooltip, width) {
    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            put("id", id)
            put("on_submit", submit.getNbt())
        }
    }
}