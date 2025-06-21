package io.github.dockyardmc.dialog

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.button.DialogButton
import io.github.dockyardmc.dialog.input.DialogInput
import io.github.dockyardmc.extentions.putList
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogEntry
import io.github.dockyardmc.registry.registries.DialogRegistry
import io.github.dockyardmc.registry.registries.DialogType
import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag

class DialogListDialog(
    override val title: String,
    override val externalTitle: String?,
    override val canCloseWithEsc: Boolean,
    override val body: List<DialogBody>,
    override val afterAction: AfterAction,
    override val inputs: Collection<DialogInput>,
    val dialogs: Collection<DialogEntry>,
    val exitAction: DialogButton?,
    val columns: Int,
    val buttonWidth: Int,
) : Dialog() {
    override val type: DialogType = DialogTypes.DIALOG_LIST

    override fun getNbt(): CompoundBinaryTag {
        var nbt = super.getNbt()

        nbt = nbt.putList("dialogs", BinaryTagTypes.STRING, dialogs.map { StringBinaryTag.stringBinaryTag(it.getEntryIdentifier()) })

        exitAction?.let {
            nbt = nbt.put("exit_action", it.getNbt())
        }
        nbt = nbt.putInt("columns", columns)
        nbt = nbt.putInt("button_width", buttonWidth)
        return nbt
    }

    @DialogDsl
    class Builder : Dialog.Builder() {
        val dialogs = mutableListOf<DialogEntry>()
        var exitAction: DialogButton? = null
        var columns: Int = 2
        var buttonWidth: Int = 150

        fun addDialog(dialog: DialogEntry) {
            dialogs.add(dialog)
        }

        override fun build(): DialogListDialog {
            return DialogListDialog(
                title,
                externalTitle,
                canCloseWithEsc,
                body.toList(),
                afterAction,
                inputs.toList(),
                dialogs.toList(),
                exitAction,
                columns,
                buttonWidth
            )
        }
    }
}

fun createDialogListDialog(id: String, block: @DialogDsl DialogListDialog.Builder.() -> Unit): DialogEntry {
    return DialogRegistry.addEntry(
        id,
        DialogListDialog.Builder().apply(block).build()
    )
}