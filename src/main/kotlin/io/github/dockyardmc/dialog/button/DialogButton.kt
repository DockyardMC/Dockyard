package io.github.dockyardmc.dialog.button

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.dialog.action.CommandTemplateDialogAction
import io.github.dockyardmc.dialog.action.CustomDialogAction
import io.github.dockyardmc.dialog.action.DialogAction
import io.github.dockyardmc.dialog.action.StaticDialogAction
import io.github.dockyardmc.scroll.ClickEvent
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DialogButton(
    override val label: String,
    override val tooltip: String?,
    override val width: Int,
    val action: DialogAction?,
) : AbstractDialogButton() {
    init {
        if (width < 1 || width > 1024) throw IllegalArgumentException("width must be between 1 and 1024 (inclusive)")
    }


    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            action?.let {
                put("action", it.getNbt())
            }
        }
    }

    @DialogDsl
    class Builder(label: String) : AbstractDialogButton.Builder(label) {
        var action: DialogAction? = null

        /**
         * @see CommandTemplateDialogAction
         */
        fun withCommandTemplate(template: String) {
            action = CommandTemplateDialogAction(template)
        }

        /**
         * @see CustomDialogAction
         */
        fun withCustomClickAction(id: String, additions: NBTCompound? = null) {
            action = CustomDialogAction(id, additions)
        }

        /**
         * @see StaticDialogAction
         */
        fun withClickEvent(clickEvent: ClickEvent) {
            action = StaticDialogAction(clickEvent)
        }

        override fun build(): DialogButton {
            return DialogButton(label, tooltip, width, action)
        }
    }
}
