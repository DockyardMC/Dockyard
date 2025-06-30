package io.github.dockyardmc.dialog.input

import io.github.dockyardmc.annotations.DialogDsl
import io.github.dockyardmc.extentions.modify
import io.github.dockyardmc.nbt.nbt
import io.github.dockyardmc.protocol.NbtWritable
import io.github.dockyardmc.registry.DialogInputTypes
import io.github.dockyardmc.registry.registries.DialogInputType
import net.kyori.adventure.nbt.CompoundBinaryTag

class TextDialogInput(
    override val key: String,
    override val label: String,
    val width: Int,
    val labelVisible: Boolean,
    val initial: String,
    val maxLength: Int,
    val multiline: Multiline?,
) : DialogInput() {
    override val type: DialogInputType = DialogInputTypes.TEXT

    init {
        require(maxLength > 0) { "maxLength must be positive" }
        require(width in 1..1024) { "width must be between 1 and 1024 (inclusive)" }
    }

    override fun getNbt(): CompoundBinaryTag {
        return super.getNbt().modify {
            withInt("width", width)
            withBoolean("label_visible", labelVisible)
            withString("initial", initial)
            withInt("max_length", maxLength)
            multiline?.let { withCompound("multiline", it.getNbt()) }
        }
    }

    class Multiline(
        val maxLines: Int?,
        val height: Int?,
    ) : NbtWritable {
        init {
            maxLines?.let {
                require(it > 0) { "maxLines must be positive" }
            }
            height?.let {
                require(it > 0) { "height must be positive" }
            }
        }

        override fun getNbt(): CompoundBinaryTag {
            return nbt {
                maxLines?.let { withInt("max_lines", maxLines) }
                height?.let { withInt("height", height) }
            }
        }

        class Builder {
            var maxLines: Int? = null
            var height: Int? = null

            fun build(): Multiline {
                return Multiline(maxLines, height)
            }
        }
    }

    @DialogDsl
    class Builder(key: String) : DialogInput.Builder(key) {
        var width: Int = 200
        var labelVisible: Boolean = true
        var initial: String = ""
        var maxLength: Int = 32
        var multiline: Multiline? = null

        inline fun useMultiline(block: Multiline.Builder.() -> Unit = {}) {
            multiline = Multiline.Builder().apply(block).build()
        }

        override fun build(): DialogInput {
            return TextDialogInput(key, label, width, labelVisible, initial, maxLength, multiline)
        }
    }
}