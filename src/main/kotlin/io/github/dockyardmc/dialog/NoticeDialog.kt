package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.button.DialogButton
import io.github.dockyardmc.dialog.input.DialogInput
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogType
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class NoticeDialog(
    override val title: String,
    override val externalTitle: String?,
    override val canCloseWithEsc: Boolean,
    override val body: List<DialogBody>,
    override val afterAction: AfterAction,
    override val inputs: Collection<DialogInput>,
    val button: DialogButton = DialogButton("<translate:'gui.ok'>"),
) : Dialog() {

    override val type: DialogType = DialogTypes.NOTICE

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("action", button.getNbt())
        }
    }
}