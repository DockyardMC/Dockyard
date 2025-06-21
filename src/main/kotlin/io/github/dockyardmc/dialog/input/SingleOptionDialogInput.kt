package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.extentions.modify
import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import io.github.dockyardmc.scroll.extensions.toComponent
import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.CompoundBinaryTag

class SingleOptionDialogInput(
    override val key: String,
    override val label: String,
    val options: Collection<Option>,
    val width: Int,
    val labelVisible: Boolean,
) : DialogInput() {
    override val type: DialogInputType = DialogInputTypes.SINGLE_OPTION

    init {
        if (options.isEmpty()) throw IllegalArgumentException("options can't be empty")
        if (width < 1 || width > 1024) throw IllegalArgumentException("width must be between 1 and 1024 (inclusive)")
    }

    override fun getNbt(): CompoundBinaryTag {
        return super.getNbt().modify {
            withInt("width", width)
            withList("options", BinaryTagTypes.COMPOUND, options.map(NbtWritable::getNbtAsCompound))
            withBoolean("label_visible", labelVisible)
        }
    }

    /**
     * @property initial if this option is initially selected
     */
    class Option(
        val id: String,
        val label: String,
        val initial: Boolean
    ) : NbtWritable {
        override fun getNbt(): CompoundBinaryTag {
            return nbt {
                withString("id", id)
                withCompound("display", label.toComponent().toNBT())
                withBoolean("initial", initial)
            }
        }

        class Builder(val id: String) {
            var label: String = ""
            var initial: Boolean = false

            fun build(): Option {
                return Option(id, label, initial)
            }
        }
    }

    @DialogDsl
    class Builder(key: String) : DialogInput.Builder(key) {
        val options = mutableListOf<Option>()
        var width: Int = 200
        var labelVisible: Boolean = true

        fun addOption(id: String, block: Option.Builder.() -> Unit) {
            options.add(Option.Builder(id).apply(block).build())
        }

        override fun build(): SingleOptionDialogInput {
            return SingleOptionDialogInput(
                key,
                label,
                options.toList(),
                width,
                labelVisible
            )
        }
    }
}