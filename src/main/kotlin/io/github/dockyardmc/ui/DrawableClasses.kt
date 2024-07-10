package io.github.dockyardmc.ui

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindablePairMap
import io.github.dockyardmc.item.EnchantmentGlintOverrideItemComponent
import io.github.dockyardmc.item.ItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Item
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.sounds.playSound

open class DrawableContainerScreen() {
    open val name: String = "Inventory"
    open val rows: Int? = 6
    val slots: BindablePairMap<Int, DrawableItemStack> = BindablePairMap()
    private var closeListener: ((Player) -> Unit)? = null
    private var openListener: ((Player) -> Unit)? = null

    fun <T> setReactive(bindable: Bindable<T>, unit: (Bindable.ValueChangedEvent<T>) -> Unit) {
        bindable.valueChanged {
            unit.invoke(it)
        }
        bindable.triggerUpdate()
    }

    fun onClose(unit: (Player) -> Unit) {
        closeListener = unit
    }

    fun onOpen(unit: (Player) -> Unit) {
        openListener = unit
    }

    fun drawableItemStack(itemStack: ItemStack): DrawableItemStack = DrawableItemStack(Bindable(itemStack))
    fun drawableItemStack(item: Item, count: Int = 1): DrawableItemStack = DrawableItemStack(Bindable(ItemStack(item, 1)))

    fun fill(from: Vector2, to: Vector2, item: DrawableItemStack) {
        for (x in from.x..to.x) {
            for (y in from.y..to.y) {
                slots[x, y] = item
            }
        }
    }
}

class DrawableReactiveListener<T>(var unit: (Bindable.ValueChangedEvent<T>) -> Unit) {
}

class DrawableClickEvent(var clickType: DrawableClickType, val item: ItemStack, val player: Player)
class DrawableClickListener(var unit: (DrawableClickEvent) -> Unit)

enum class DrawableClickType {
    LEFT_CLICK,
    RIGHT_CLICK,
    LEFT_CLICK_SHIFT,
    RIGHT_CLICK_SHIFT,
    MIDDLE_CLICK,
    HOTKEY,
    DROP,
}

data class DrawableItemStack(
    val itemStack: Bindable<ItemStack> = Bindable(ItemStack.air),
) {

    val name: String get() = itemStack.value.displayName.value
    val lore: List<String> get() = itemStack.value.lore.values
    val customModelData: Int get() = itemStack.value.customModelData.value
    val components: List<ItemComponent> get() = itemStack.value.components.values

    val clickListeners: MutableList<DrawableClickListener> = mutableListOf()

    fun withName(name: String): DrawableItemStack {
        itemStack.value.displayName.value = name
        return this
    }

    fun addLoreLine(line: String): DrawableItemStack {
        itemStack.value.lore.add(line)
        return this
    }

    fun withComponent(component: ItemComponent): DrawableItemStack {
        itemStack.value.components.add(component)
        return this
    }

    fun onClick(unit: (DrawableClickEvent) -> Unit): DrawableItemStack {
        clickListeners.add(DrawableClickListener(unit))
        return this
    }
}

class CookieClickerScreen: DrawableContainerScreen() {
    override val name: String = "<orange>Cookie Clicker"
    override val rows: Int = 3

    val cookies = Bindable<Int>(0)

    init {

        onOpen { it.playSound("minecraft:block.wooden_button.click_on") }
        onClose { it.playSound("minecraft:block.wooden_button.click_off") }

        fill(Vector2(0, 0), Vector2(8, 3), drawableItemStack(Items.BLACK_STAINED_GLASS_PANE))

        setReactive<Int>(cookies) { update ->
            slots[4, 1] = drawableItemStack(Items.COOKIE, update.newValue)
                .withName("<yellow><bold>CLICK! <white>The Cookie")
                .addLoreLine("")
                .addLoreLine("<gray>You currently have <aqua>${update.newValue} cookies<gray>!")
                .addLoreLine("")
                .withComponent(EnchantmentGlintOverrideItemComponent(true))
                .onClick { click ->
                    cookies.value++
                    click.player.playSound("minecraft:entity.generic.eat")
                }
        }
    }
}