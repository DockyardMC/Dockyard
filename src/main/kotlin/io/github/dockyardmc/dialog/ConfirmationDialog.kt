package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.button.DialogButton
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogType
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class ConfirmationDialog(
    override val title: String,
    override val externalTitle: String?,
    override val canCloseWithEsc: Boolean,
    override val body: List<DialogBody>,
    val yes: DialogButton,
    val no: DialogButton,
) : Dialog() {
    override val type: DialogType = DialogTypes.CONFIRMATION

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("yes", yes.getNbt())
            put("no", no.getNbt())
        }
    }
}