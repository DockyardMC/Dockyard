package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.button.DialogButton
import io.github.dockyardmc.dialog.input.DialogInput
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTType

class DialogListDialog(
    override val title: String,
    override val externalTitle: String?,
    override val canCloseWithEsc: Boolean,
    override val body: List<DialogBody>,
    override val afterAction: AfterAction,
    override val inputs: Collection<DialogInput>,
    val dialogs: Collection<Dialog>,
    val exitAction: DialogButton? = null,
    val columns: Int = 2,
    val buttonWidth: Int = 150,
) : Dialog() {
    override val type: DialogType = DialogTypes.DIALOG_LIST

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("dialogs", NBTList(NBTType.TAG_Compound, dialogs.map(NbtWritable::getNbt)))
            exitAction?.let {
                put("exit_action", it.getNbt())
            }
            put("columns", columns)
            put("button_width", buttonWidth)
        }
    }
}