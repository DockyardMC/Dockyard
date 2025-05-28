package io.github.dockyardmc.dialog.action

import io.github.dockyardmc.registry.registries.DialogActionType
import io.github.dockyardmc.registry.registries.DialogActionTypeRegistry
import io.github.dockyardmc.scroll.ClickEvent
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class StaticDialogAction(
    val clickEvent: ClickEvent
) : DialogAction() {
    override val type: DialogActionType = DialogActionTypeRegistry[clickEvent.action]

    override fun getNbt(): NBTCompound {
        // it writes the type on its own c:
        // however, if you modify the parent class in any way you'd expect this one to inherit,
        // so I'll keep it that way
        return super.getNbt().kmodify {
            putAll(clickEvent.getNbt())
        }
    }
}