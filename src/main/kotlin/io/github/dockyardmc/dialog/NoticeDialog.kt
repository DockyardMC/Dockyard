package io.github.dockyardmc.dialog

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.button.DialogButton
import io.github.dockyardmc.dialog.input.DialogInput
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogEntry
import io.github.dockyardmc.registry.registries.DialogRegistry
import io.github.dockyardmc.registry.registries.DialogType
import net.kyori.adventure.nbt.CompoundBinaryTag

class NoticeDialog(
    override val title: String,
    override val externalTitle: String?,
    override val canCloseWithEsc: Boolean,
    override val body: List<DialogBody>,
    override val afterAction: AfterAction,
    override val inputs: Collection<DialogInput>,
    val button: DialogButton,
) : Dialog() {
    override val type: DialogType = DialogTypes.NOTICE

    override fun getNbt(): CompoundBinaryTag {
        return super.getNbt().put("action", button.getNbt())
    }

@DialogDsl
class Builder : Dialog.Builder() {
    var button: DialogButton = DialogButton("<translate:'gui.ok'>", null, 150, null)

    fun withButton(label: String, block: (DialogButton.Builder.() -> Unit)? = null) {
        button = DialogButton.Builder(label).apply {
            block?.let { apply(it) }
        }.build()
    }

    override fun build(): NoticeDialog {
        return NoticeDialog(
            title,
            externalTitle,
            canCloseWithEsc,
            body.toList(),
            afterAction,
            inputs.toList(),
            button
        )
    }
}
}

fun createNoticeDialog(id: String, block: @DialogDsl NoticeDialog.Builder.() -> Unit): DialogEntry {
    return DialogRegistry.addEntry(
        id,
        NoticeDialog.Builder().apply(block).build()
    )
}