package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.button.DialogButton
import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogType
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class ConfirmationDialog(
    title: String,
    externalTitle: String?,
    canCloseWithEsc: Boolean,
    body: List<DialogBody>,
    val yes: DialogButton,
    val no: DialogButton
) : Dialog(title, externalTitle, canCloseWithEsc, body) {
    override val type: DialogType = DialogTypes.CONFIRMATION

    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            put("yes", yes.getNbt())
            put("no", no.getNbt())
        }
    }
}