package io.github.dockyardmc.advancement

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.scroll.Component
import io.netty.buffer.ByteBuf

/**
 * @param background can use [getTextureId] to get one
 * @param icon icon of the advancement is an item, it could have enchantements, etc. IT CANNOT BE AIR OR ELSE CLIENT JUST CRASHES.
 *
 * @throws IllegalArgumentException if icon is air
 */
data class AdvancementDisplay(
    val title: Component,
    val description: Component,
    val icon: ItemStack,
    val frame: AdvancementFrame = AdvancementFrame.TASK,
    val showToast: Boolean = true,
    val isHidden: Boolean = false,
    val background: String? = null,
    val x: Float,
    val y: Float,
) : NetworkWritable {
    init {
        if(icon.material == Items.AIR) throw IllegalArgumentException("advancement icon can't be air")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeTextComponent(title)
        buffer.writeTextComponent(description)
        icon.write(buffer)
        buffer.writeVarInt(frame.ordinal)
        buffer.writeInt(getFlags())
        background?.let(buffer::writeString)
        buffer.writeFloat(x)
        buffer.writeFloat(y)
    }

    fun getFlags(): Int {
        var flags = 0x0
        if(background != null) {
            flags = flags or HAS_BACKGROUND_TEXTURE
        }
        if(showToast) {
            flags = flags or SHOW_TOAST
        }
        if(isHidden) {
            flags = flags or HIDDEN
        }
        return flags
    }

    companion object {
        const val HAS_BACKGROUND_TEXTURE = 0x01
        const val SHOW_TOAST = 0x02
        const val HIDDEN = 0x04
    }
}

enum class AdvancementFrame {
    TASK,
    CHALLENGE,
    GOAL;
}

