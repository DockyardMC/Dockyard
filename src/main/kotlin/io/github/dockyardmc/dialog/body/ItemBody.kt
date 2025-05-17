package io.github.dockyardmc.dialog.body

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.DialogBodyTypes
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound

/**
 * @param showDecorations show item count and durability
 * @param showTooltip show tooltip for the item
 */
data class ItemBody(
    val item: ItemStack,
    val description: PlainMessage? = null,
    val showDecorations: Boolean = true,
    val showTooltip: Boolean = true,
    val width: Int = 16,
    val height: Int = 16,
) : DialogBody {

    override fun getNbt(): NBTCompound {
        return NBT.Compound { builder ->
            builder.put("type", DialogBodyTypes.ITEM.getEntryIdentifier())

            builder.put("item", item.getNbt())
            description?.let {
                builder.put("description", it.getNbt())
            }
            builder.put("show_decorations", showDecorations)
            builder.put("show_tooltip", showTooltip)
            builder.put("width", width)
            builder.put("height", height)
        }
    }
}