package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class BooleanDialogInput(
    label: String,
    val initial: Boolean = false,
    val onTrue: String = "true",
    val onFalse: String = "false"
) : DialogInput(label) {
    override val type: DialogInputType = DialogInputTypes.BOOLEAN

    override fun getNbt(): NBT {
        return (super.getNbt() as NBTCompound).kmodify {
            put("initial", initial)
            put("on_true", onTrue)
            put("on_false", onFalse)
        }
    }
}