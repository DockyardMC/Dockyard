package io.github.dockyardmc.dialog.button

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

sealed class AbstractDialogButton : NbtWritable {
    abstract val label: String
    abstract val tooltip: String?
    abstract val width: Int

    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("label", label.toComponent().toNBT())
            tooltip?.let {
                builder.put("tooltip", it.toComponent().toNBT())
            }
            builder.put("width", width)
        }
    }

    @DialogDsl
    sealed class Builder(val label: String) {
        var tooltip: String? = null
        var width: Int = 150

        abstract fun build(): AbstractDialogButton
    }
}
