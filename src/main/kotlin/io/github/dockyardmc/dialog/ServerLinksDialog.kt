package io.github.dockyardmc.dialog

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.button.DialogButton
import io.github.dockyardmc.dialog.input.DialogInput
import io.github.dockyardmc.extentions.modify
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogEntry
import io.github.dockyardmc.registry.registries.DialogRegistry
import io.github.dockyardmc.registry.registries.DialogType
import net.kyori.adventure.nbt.CompoundBinaryTag

class ServerLinksDialog(
    override val title: String,
    override val externalTitle: String?,
    override val canCloseWithEsc: Boolean,
    override val body: List<DialogBody>,
    override val afterAction: AfterAction,
    override val inputs: Collection<DialogInput>,
    val exitAction: DialogButton? = null,
    val columns: Int = 2,
    val buttonWidth: Int = 150,
) : Dialog() {
    override val type: DialogType = DialogTypes.SERVER_LINKS

    override fun getNbt(): CompoundBinaryTag {
        return super.getNbt().modify {
            exitAction?.let {
                withCompound("exit_action", it.getNbt())
            }
            withInt("columns", columns)
            withInt("button_width", buttonWidth)
        }
    }

    @DialogDsl
    class Builder : Dialog.Builder() {
        var exitAction: DialogButton? = null
        var columns: Int = 2
        var buttonWidth: Int = 150

        override fun build(): ServerLinksDialog {
            return ServerLinksDialog(
                title,
                externalTitle,
                canCloseWithEsc,
                body.toList(),
                afterAction,
                inputs.toList(),
                exitAction,
                columns,
                buttonWidth
            )
        }
    }
}

fun createServerLinksDialog(id: String, block: @DialogDsl ServerLinksDialog.Builder.() -> Unit): DialogEntry {
    return DialogRegistry.addEntry(
        id,
        ServerLinksDialog.Builder().apply(block).build()
    )
}