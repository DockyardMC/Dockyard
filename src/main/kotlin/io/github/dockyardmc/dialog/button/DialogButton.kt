package io.github.dockyardmc.dialog.button

import io.github.dockyardmc.scroll.ClickAction
import io.github.dockyardmc.scroll.ClickEvent
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DialogButton(
    label: String,
    tooltip: String? = null,
    width: Int = 150,
    val onClick: ClickEvent? = null,
) : AbstractDialogButton(label, tooltip, width) {
    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            if (onClick != null)
                put("on_click", NBT.Compound { builder ->
                    builder.put("action", onClick.action.name.lowercase())
                    builder.put(when (onClick.action) {
                        ClickAction.OPEN_URL -> "url"
                        ClickAction.RUN_COMMAND -> "command"
                        ClickAction.SUGGEST_COMMAND -> "command"
                        ClickAction.CHANGE_PAGE -> "page"
                        ClickAction.COPY_TO_CLIPBOARD -> "value"

                        // seriously, open_file just doesn't?? and I cant find it in older versions
                        else -> throw IllegalArgumentException("unfortunately ${onClick.action.name} doesn't exist")
                    }, onClick.value)
                })
        }
    }
}
