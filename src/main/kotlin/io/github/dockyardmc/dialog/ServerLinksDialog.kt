package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogType
import io.github.dockyardmc.scroll.ClickEvent
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class ServerLinksDialog(
    title: String,
    externalTitle: String?,
    canCloseWithEsc: Boolean,
    body: List<DialogBody>,
    val onCancel: ClickEvent? = null,
    val columns: Int = 2,
    val buttonWidth: Int = 150,
) : Dialog(title, externalTitle, canCloseWithEsc, body) {
    override val type: DialogType = DialogTypes.SERVER_LINKS

    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            if(onCancel != null)
                put("on_cancel", Component(clickEvent = onCancel).toNBT())
            put("columns", columns)
            put("button_width", buttonWidth)
        }
    }
}