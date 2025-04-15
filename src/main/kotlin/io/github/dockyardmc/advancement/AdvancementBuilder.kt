package io.github.dockyardmc.advancement

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.vectors.Vector2f
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.scroll.extensions.toComponent

/**
 * Build the [Advancement] WITH [AdvancementDisplay]
 */
class AdvancementBuilder(val id: String) {
    var parent: Advancement? = null
    val requirements = mutableListOf<List<String>>()

    var title: String = ""
    var description: String = ""
    var icon: ItemStack = Items.PAPER.toItemStack()
    var frame: AdvancementFrame = AdvancementFrame.TASK
    var showToast: Boolean = true
    var isHidden: Boolean = false
    var background: String? = null

    var x: Float = 0f
    var y: Float = 0f

    fun withParent(parent: Advancement) {
        this.parent = parent
    }

    fun withTitle(title: String) {
        this.title = title
    }

    fun withDescription(description: String) {
        this.description = description
    }

    fun withIcon(icon: ItemStack) {
        this.icon = icon
    }

    fun withIcon(icon: Item) {
        this.icon = icon.toItemStack()
    }

    fun withFrame(frame: AdvancementFrame) {
        this.frame = frame
    }

    fun useToast(showToast: Boolean) {
        this.showToast = showToast
    }

    fun withHidden(isHidden: Boolean) {
        this.isHidden = isHidden
    }

    /**
     * @param background path to a texture in minecraft resource pack
     *
     * Some examples:
     * - `minecraft:textures/item/stick.png`
     * - `minecraft:textures/block/`
     * - `minecraft:textures/gui/book.png` (looks bad but works)
     */
    fun withBackground(background: String?) {
        this.background = background
    }

    fun withBackground(block: RegistryBlock) {
        val blockId = block.identifier.removePrefix("minecraft:")

        // this is how it should be in 1.21.5
        // "block/$blockId"
        this.background = "minecraft:textures/block/$blockId.png"
    }

    fun withBackground(background: Item) {
        val id = background.identifier.removePrefix("minecraft:")
        this.background = "minecraft:textures/item/$id.png"
    }

    fun withPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun withPosition(vec: Vector2f) {
        this.x = vec.x
        this.y = vec.y
    }

    fun withRequirement(req: String) {
        this.requirements += listOf(req)
    }

    fun withRequirementsAnyOf(requirements: List<String>) {
        this.requirements += requirements
    }

    fun build(): Advancement {
        return Advancement(
            id, parent, AdvancementDisplay(
                title.toComponent(), description.toComponent(), icon, frame, showToast, isHidden, background, x, y
            ), requirements
        )
    }
}

fun advancement(id: String, builder: AdvancementBuilder.() -> Unit): Advancement {
    val adv = AdvancementBuilder(id)
    builder.invoke(adv)
    return adv.build()
}