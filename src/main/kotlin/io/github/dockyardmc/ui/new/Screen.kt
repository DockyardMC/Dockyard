package io.github.dockyardmc.ui.new

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundOpenContainerPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetContainerSlotPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ScreenSize

abstract class Screen : CompositeDrawable() {

    abstract val rows: Int
    open val name: String = "Screen(${this::class.simpleName})"
    lateinit var player: Player

    open fun onOpen() {}
    open fun onClose() {}
    open fun onClick(slot: Int, clickType: DrawableItemStack.ClickType) {}
    open fun onRerender() {}

    class InvalidScreenSlotOperationException(override val message: String) : Exception(message)

    fun open(player: Player) {
        this.player = player

        player.sendPacket(ClientboundOpenContainerPacket(getScreenSize().inventoryType, name))

        player.currentlyOpenScreen = this
        onRenderInternal()
        update(player)
        onOpen()
    }

    fun getScreenSize(): ScreenSize {
        val rowsMax = rows.coerceIn(1, 6)
        return ScreenSize.valueOf("GENERIC_9X${rowsMax}")
    }

    fun update(player: Player) {
        getSlots().forEach { (slot, item) ->
            if (slot > getScreenSize().getModifiableSlots() - 1) {
                player.closeInventory()
                throw InvalidScreenSlotOperationException("Slot $slot is out of bounds for screen with ${getScreenSize().getModifiableSlots() - 1} slots (${getScreenSize().rows} rows)")
            }
            player.sendPacket(ClientboundSetContainerSlotPacket(slot, item.itemStack))
        }
        onRerender()
    }

    fun onClick(slot: Int, player: Player, clickType: DrawableItemStack.ClickType) {
        val clickedSlot = getSlots()[slot]

        if (clickedSlot == null) {
            update(player)
            return
        }

        try {
            clickedSlot.onClick?.invoke(player, clickType)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            throw ex
        }

        update(player)
    }

    override fun dispose() {
        onClose()
        getChildren().forEach { (child, _) ->
            child.dispose()
        }
    }
}