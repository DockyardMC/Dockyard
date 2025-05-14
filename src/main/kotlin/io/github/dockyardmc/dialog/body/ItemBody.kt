package io.github.dockyardmc.dialog.body

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.DialogBodyTypes
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBT

data class ItemBody(
    val item: ItemStack,
    val description: PlainMessage? = null,
    val showDecorations: Boolean = true,
    val showTooltip: Boolean = true,
    val width: Int = 16,
    val height: Int = 16
) : DialogBody {

    override fun getNbt(): NBT {
        return NBT.Compound { builder ->
            builder.put("type", DialogBodyTypes.ITEM.getEntryIdentifier())

            builder.put("item", item.getNbt())
            if(description != null)
                builder.put("description", description.getNbt())
            builder.put("show_decorations", showDecorations)
            builder.put("show_tooltip", showTooltip)
            builder.put("width", width)
            builder.put("height", height)
        }
    }
}