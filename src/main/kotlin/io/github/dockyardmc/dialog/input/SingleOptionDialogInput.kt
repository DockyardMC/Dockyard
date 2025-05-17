package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTType

class SingleOptionDialogInput(
    override val label: String,
    val options: Collection<Option>,
    val width: Int = 200,
    val labelVisible: Boolean = true,
) : DialogInput() {
    override val type: DialogInputType = DialogInputTypes.SINGLE_OPTION

    init {
        if(options.isEmpty()) throw IllegalArgumentException("options can't be empty")
    }

    override fun getNbt(): NBTCompound {
        return super.getNbt().kmodify {
            put("width", width)
            put("options", NBTList(NBTType.TAG_Compound, options.map(NbtWritable::getNbt)))
            put("label_visible", labelVisible)
        }
    }

    class Option(
        val id: String,
        val display: String,
        val initial: Boolean
    ) : NbtWritable {
        override fun getNbt(): NBT {
            return NBT.Compound { builder ->
                builder.put("id", id)
                builder.put("display", display.toComponent().toNBT())
                builder.put("initial", initial)
            }
        }
    }
}