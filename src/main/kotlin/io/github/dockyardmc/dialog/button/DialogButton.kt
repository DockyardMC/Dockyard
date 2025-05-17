package io.github.dockyardmc.dialog.button

import io.github.dockyardmc.scroll.ClickEvent
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DialogButton(
    override val label: String,
    override val tooltip: String? = null,
    override val width: Int = 150,
    val onClick: ClickEvent? = null,
) : AbstractDialogButton() {
    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            onClick?.let {
                put("on_click", it.getNbt())
            }
        }
    }
}
