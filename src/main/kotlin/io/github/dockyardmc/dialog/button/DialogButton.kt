package io.github.dockyardmc.dialog.button

import io.github.dockyardmc.scroll.ClickEvent
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DialogButton(
    label: String,
    tooltip: String? = null,
    width: Int = 150,
    val onClick: ClickEvent? = null,
) : AbstractDialogButton(label, tooltip, width) {
    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            if (onClick != null)
                put("on_click", onClick.getNbt())
        }
    }
}
