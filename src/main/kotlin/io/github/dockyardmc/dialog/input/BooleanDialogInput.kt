package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class BooleanDialogInput(
    override val key: String,
    override val label: String,
    val initial: Boolean,
    val onTrue: String,
    val onFalse: String,
) : DialogInput() {
    override val type: DialogInputType = DialogInputTypes.BOOLEAN

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("initial", initial)
            put("on_true", onTrue)
            put("on_false", onFalse)
        }
    }

    @DialogDsl
    class Builder(key: String) : DialogInput.Builder(key) {
        var initial: Boolean = false
        var onTrue: String = "true"
        var onFalse: String = "false"

        override fun build(): BooleanDialogInput {
            return BooleanDialogInput(key, label, initial, onTrue, onFalse)
        }
    }
}