package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.button.DialogButton
import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogType
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class NoticeDialog(
    title: String,
    externalTitle: String?,
    canCloseWithEsc: Boolean,
    body: List<DialogBody>,
    val button: DialogButton = DialogButton("<translate:'gui.ok'>")
) : Dialog(title, externalTitle, canCloseWithEsc, body) {

    override val type: DialogType = DialogTypes.NOTICE

    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            put("action", button.getNbt())
        }
    }
}