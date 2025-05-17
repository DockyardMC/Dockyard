package io.github.dockyardmc.dialog

import io.github.dockyardmc.dialog.body.DialogBody
import io.github.dockyardmc.dialog.button.DialogButton
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.DialogTypes
import io.github.dockyardmc.registry.registries.DialogType
import io.github.dockyardmc.scroll.ClickEvent
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTType

class MultiActionDialog(
    override val title: String,
    override val externalTitle: String?,
    override val canCloseWithEsc: Boolean,
    override val body: List<DialogBody>,
    val actions: Collection<DialogButton>,
    val onCancel: ClickEvent? = null,
    val columns: Int = 2,
) : Dialog() {
    override val type: DialogType = DialogTypes.MULTI_ACTION

    init {
        if(actions.isEmpty()) throw IllegalArgumentException("actions can't be empty")
    }

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("actions", NBTList(NBTType.TAG_Compound, actions.map(NbtWritable::getNbt)))
            onCancel?.let {
                put("on_cancel", it.getNbt())
            }
            put("columns", columns)
        }
    }
}