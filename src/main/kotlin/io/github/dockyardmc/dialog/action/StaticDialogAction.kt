package io.github.dockyardmc.dialog.action

import io.github.dockyardmc.registry.registries.DialogActionType
import io.github.dockyardmc.registry.registries.DialogActionTypeRegistry
import io.github.dockyardmc.scroll.ClickEvent
import org.jglrxavpok.hephaistos.nbt.NBTCompound

/**
 * Just a wrapper for [ClickEvent]
 *
 * Yes. the client will just do whatever the click event is supposed to do.
 *
 * With an exception `open_file`. it's not allowed from a server
 */
class StaticDialogAction(
    val clickEvent: ClickEvent
) : DialogAction() {
    override val type: DialogActionType = DialogActionTypeRegistry[clickEvent.action]

    init {
        if (clickEvent is ClickEvent.OpenFile) {
            throw IllegalArgumentException("open_file is not allowed from a server")
        }
    }

    override fun getNbt(): NBTCompound {
        // it writes the type on its own c:
        // however, if you modify the parent class in any way you'd expect this one to inherit,
        // so I'll keep it that way
        return super.getNbt().kmodify {
            putAll(clickEvent.getNbt())
        }
    }
}