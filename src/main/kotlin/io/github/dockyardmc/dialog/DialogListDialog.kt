package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogType
import io.github.dockyardmc.scroll.ClickEvent
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTType

class DialogListDialog(
    title: String,
    externalTitle: String?,
    canCloseWithEsc: Boolean,
    body: List<DialogBody>,
    val dialogs: Collection<Dialog>,
    val onCancel: ClickEvent? = null,
    val columns: Int = 2,
    val buttonWidth: Int = 150
) : Dialog(title, externalTitle, canCloseWithEsc, body) {
    override val type: DialogType = DialogTypes.DIALOG_LIST

    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            put("dialogs", NBTList(NBTType.TAG_Compound, dialogs.map(NbtWritable::getNbt)))
            if(onCancel != null)
                put("on_cancel", onCancel.getNbt())
            put("columns", columns)
            put("button_width", buttonWidth)
        }
    }
}