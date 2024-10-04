package io.github.dockyardmc.ui

import cz.lukynka.Bindable
import io.github.dockyardmc.bindables.BindablePairMap
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.inventory.ContainerInventory
import io.github.dockyardmc.item.EnchantmentGlintOverrideItemComponent
import io.github.dockyardmc.item.ItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.MaxStackSizeItemComponent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundOpenContainerPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetInventorySlotPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.InventoryType
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.Disposable

open class DrawableContainerScreen: ContainerInventory, Disposable {
    var player: Player? = null
    override val name: String = "Inventory"
    override val rows: Int = 6
    override var innerContainerContents: MutableMap<Int, ItemStack> = mutableMapOf()
    val slots: BindablePairMap<Int, DrawableItemStack> = BindablePairMap()
    var closeListener: ((Player) -> Unit)? = null
    var openListener: ((Player) -> Unit)? = null
    val screenEventPool = EventPool()

    override fun open(player: Player) {
        this.player = player
        player.sendPacket(ClientboundOpenContainerPacket(InventoryType.valueOf("GENERIC_9X$rows"), name))
        openListener?.invoke(player)
        player.currentOpenInventory.value = this
        slots.triggerUpdate()
    }

    init {
        slots.mapUpdated {
            innerContainerContents = slots.values.mapValues { it.value.itemStack.value }
                .mapKeys { getSlotIndexFromVector2(it.key.first, it.key.second) }.toMutableMap()
            slots.values.forEach {
                val index = getSlotIndexFromVector2(it.key.first, it.key.second)
                val packet = ClientboundSetInventorySlotPacket(1, 0, index, it.value.itemStack.value)
                player?.sendPacket(packet)
            }
        }
    }

    fun getSlotIndexFromVector2(x: Int, y: Int): Int {
        require(!(x < 0 || y < 0)) { "Coordinates cannot be negative" }
        return x + (y * 9)
    }

    fun <T> setReactive(bindable: Bindable<T>, unit: (Bindable.ValueChangedEvent<T>) -> Unit) {
        bindable.valueChanged {
            unit.invoke(it)
        }
        bindable.triggerUpdate()
    }

    fun click(slot: Int, player: Player) {
        slots.values.forEach {
            val slotIndex = getSlotIndexFromVector2(it.key.first, it.key.second)
            if(slotIndex != slot) return@forEach
            val listeners = it.value.clickListeners
            if(listeners.isEmpty()) return@forEach
            listeners.forEach { listener -> listener.unit.invoke(DrawableClickEvent(DrawableClickType.LEFT_CLICK, it.value.itemStack.value, player)) }
        }
    }

    fun onClose(unit: (Player) -> Unit) {
        closeListener = unit
    }

    fun onOpen(unit: (Player) -> Unit) {
        openListener = unit
    }

    fun fill(from: Vector2, to: Vector2, item: DrawableItemStack) {
        for (x in from.x..to.x) {
            for (y in from.y..to.y) {
                slots[x, y] = item
            }
        }
    }

    override fun dispose() {
        screenEventPool.dispose()
    }
}

class DrawableReactiveListener<T>(var unit: (Bindable.ValueChangedEvent<T>) -> Unit)
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
    constructor(itemStack: ItemStack): this(Bindable(itemStack))
    constructor(item: Item, count: Int = 1): this(Bindable(ItemStack(item, count)))

    val name: String get() = itemStack.value.displayName.value
    val lore: Collection<String> get() = itemStack.value.lore.values
    val customModelData: Int get() = itemStack.value.customModelData.value
    val components: Collection<ItemComponent> get() = itemStack.value.components.values

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
    override val name: String = "<black><bold>Cookie Clicker"
    override val rows: Int = 3

    val cookies = Bindable<Int>(0)

    init {

        onOpen { it.playSound("minecraft:block.wooden_button.click_on") }
        onClose { it.playSound("minecraft:block.wooden_button.click_off") }

        fill(Vector2(0, 0), Vector2(8, 2), DrawableItemStack(Items.BLACK_STAINED_GLASS_PANE))

        setReactive<Int>(cookies) { update ->
            slots[4, 1] = DrawableItemStack(Items.COOKIE, update.newValue)
                .withName("<yellow><bold>CLICK! <white>The Cookie")
                .addLoreLine("")
                .addLoreLine("<gray>You currently have <aqua>${update.newValue} cookies<gray>!")
                .addLoreLine("")
                .withComponent(MaxStackSizeItemComponent(255))
                .withComponent(EnchantmentGlintOverrideItemComponent(true))
                .onClick { click ->
                    cookies.value++
                    click.player.playSound("minecraft:entity.generic.eat")
                }
        }
    }
}