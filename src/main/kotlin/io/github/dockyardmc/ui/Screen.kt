package io.github.dockyardmc.ui

import cz.lukynka.prettylog.log
import io.github.dockyardmc.events.*
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundOpenContainerPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundSetContainerSlotPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.ScreenSize
import io.github.dockyardmc.scheduler.runLaterAsync
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.ui.snapshot.InventorySnapshot
import io.github.dockyardmc.utils.InstrumentationUtils
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.utils.getPlayerEventContext

abstract class Screen : CompositeDrawable() {

    var isFullscreen: Boolean = false
        protected set

    protected var name: String = "Screen(${this::class.simpleName})"
    protected var rows: Int = 6
    lateinit var player: Player

    open fun onOpen() {}
    open fun onClose() {}
    open fun onClick(slot: Int, clickType: DrawableItemStack.ClickType) {}
    open fun onRerender() {}

    lateinit var inventorySnapshot: InventorySnapshot
    private var hotReloadHook: EventListener<Event>? = null

    class InvalidScreenSlotOperationException(override val message: String) : Exception(message)

    fun open(player: Player) {
        this.player = player

        val event = PlayerScreenOpenEvent(player, this, getPlayerEventContext(player))
        Events.dispatch(event)
        if (event.cancelled) return

        inventorySnapshot = InventorySnapshot(player)
        if (isFullscreen) {
            player.clearInventory()
        }

        player.currentlyOpenScreen = this

        onRenderInternal()

        player.sendPacket(ClientboundOpenContainerPacket(getScreenSize().inventoryType, name))

        update(player)
        onOpen()


        if (InstrumentationUtils.isDebuggerAttached()) {
            hotReloadHook = Events.on<InstrumentationHotReloadEvent> { instrumentationHotReloadEvent ->
                if (instrumentationHotReloadEvent.kclass == this::class) {
                    player.closeInventory()

                    runLaterAsync(1.ticks) {
                        try {
                            rebuildSelfAndChildren()
                            buildComponent()
                            renderChildren()
                            open(player)
                        } catch (exception: Exception) {
                            player.sendMessage("<red>Failed to rebuild screen: $exception")
                            log(exception)
                        }
                    }
                }
            }
            debug("Registered hot reload event listener to ${this::class.simpleName}", true)
        }
    }

    fun getScreenSize(): ScreenSize {
        return ScreenSize.valueOf("GENERIC_9X${rows.coerceIn(1, 6)}")
    }

    fun update(player: Player) {
        getSlots().forEach { (slot, item) ->
            if (slot > getScreenSize().getModifiableSlots(this) - 1) {
                player.closeInventory()
                throw InvalidScreenSlotOperationException("Slot $slot is out of bounds for screen with ${getScreenSize().getModifiableSlots(this) - 1} slots (${getScreenSize().rows} rows)")
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

    fun withScreenRows(rows: Int) {
        this.rows = rows
    }

    fun withScreenName(name: String) {
        this.name = name
    }

    fun withScreenFullscreen(isFullscreen: Boolean) {
        this.isFullscreen = isFullscreen
    }

    override fun dispose() {
        onClose()
        getChildren().forEach { (child, _) ->
            child.dispose()
        }
        player.currentlyOpenScreen = null
        if (isFullscreen) inventorySnapshot.restoreAndDispose()
        if (hotReloadHook != null) {
            Events.unregister(hotReloadHook!!)
            debug("Hot Reload listener unregistered from ${this::class.simpleName}", true)
        }
        Events.dispatch(PlayerScreenCloseEvent(player, this, getPlayerEventContext(player)))
    }
}