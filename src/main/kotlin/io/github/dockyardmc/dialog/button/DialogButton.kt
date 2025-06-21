package io.github.dockyardmc.dialog.button

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.dialog.action.CommandTemplateDialogAction
import io.github.dockyardmc.dialog.action.CustomDialogAction
import io.github.dockyardmc.dialog.action.DialogAction
import io.github.dockyardmc.dialog.action.StaticDialogAction
import io.github.dockyardmc.events.DialogCustomClickActionEvent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.scroll.ClickEvent
import io.github.dockyardmc.utils.debug
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.util.*

class DialogButton(
    override val label: String,
    override val tooltip: String?,
    override val width: Int,
    val action: DialogAction?,
) : AbstractDialogButton() {

    init {
        if (width < 1 || width > 1024) throw IllegalArgumentException("width must be between 1 and 1024 (inclusive)")
    }

    override fun getNbt(): CompoundBinaryTag {
        var nbt = super.getNbt()
        action?.let {
            nbt = nbt.put("action", it.getNbt())
        }
        return nbt
    }

    @DialogDsl
    class Builder(label: String) : AbstractDialogButton.Builder(label) {
        var action: DialogAction? = null
        private val callbacks: MutableMap<String, (Player, CompoundBinaryTag) -> Unit> = mutableMapOf()

        /**
         * @see CommandTemplateDialogAction
         */
        fun withCommandTemplate(template: String) {
            action = CommandTemplateDialogAction(template)
        }

        /**
         * @see CustomDialogAction
         */
        fun withCustomClickAction(id: String, additions: CompoundBinaryTag? = null) {
            action = CustomDialogAction(id, additions)
        }

        fun onClick(callback: (DialogCustomClickActionEvent) -> Unit) {
            val id = "dockyard:${UUID.randomUUID()}"
            action = CustomDialogAction(id, null)

            Events.on<DialogCustomClickActionEvent> { event ->
                debug("recieved $event", true)
                if (event.id != id) return@on
                callback.invoke(event)
            }
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
