package io.github.dockyardmc.ui.new

import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.ui.new.components.FillFlowContainer
import io.github.dockyardmc.ui.new.components.StaticDrawableItemComponent

class CookieClickerScreen : Screen() {

    override val rows: Int = 5

    override fun buildComponent() {
        withComposite(4, 2, CookieComponent())

        withComposite(
            0, 0, FillFlowContainer(
                FillFlowContainer.Direction.DOWN, listOf(
                    CloseScreenComponent(),
                    StaticDrawableItemComponent(DrawableItem(Items.BAMBOO.toItemStack(), null)),
                    StaticDrawableItemComponent(DrawableItem(Items.AMETHYST_SHARD.toItemStack(), null)),
                    StaticDrawableItemComponent(DrawableItem(Items.AXOLOTL_BUCKET.toItemStack(), null)),
                    StaticDrawableItemComponent(DrawableItem(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE.toItemStack(), null)),
                )
            )
        )
    }
}