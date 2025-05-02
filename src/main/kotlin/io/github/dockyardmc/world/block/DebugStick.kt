package io.github.dockyardmc.world.block

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockBreakEvent
import io.github.dockyardmc.events.PlayerBlockRightClickEvent
import io.github.dockyardmc.implementations.DefaultImplementationModule
import io.github.dockyardmc.item.DebugStickItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.getOrNull
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.RegistryBlockState
import io.github.dockyardmc.scroll.extensions.put
import org.jglrxavpok.hephaistos.nbt.NBTCompound

class DebugStick : DefaultImplementationModule {

    override fun register() {
        Events.on<PlayerBlockRightClickEvent> { event ->
            val player = event.player
            val item = event.player.mainHandItem
            val block = event.block
            val location = event.location

            if (item.material != Items.DEBUG_STICK) return@on
            if (!player.hasPermission("dockyard.debug_stick")) return@on
            if (block.registryBlock.states.isEmpty()) return@on

            event.cancel()

            val state = getOrCreateLeftClickState(player, item, block, false)
            val currentValue = block.blockStates[state.name]!!

            val nextValue: String
            if (state.type == "bool") {
                nextValue = (!currentValue.toBooleanStrict()).toString()
            } else {
                val currentIndex = state.values?.indexOf(currentValue) ?: return@on
                val nextValueIndex = (currentIndex + 1) % state.values.size
                nextValue = state.values[nextValueIndex]
            }

            location.setBlock(block.withBlockStates(state.name to nextValue))
            player.sendActionBar("\"${state.name}\" to $nextValue")
        }

        Events.on<PlayerBlockBreakEvent> { event ->
            val player = event.player
            val item = event.player.inventory[player.heldSlotIndex.value]
            val block = event.block

            if (item.material != Items.DEBUG_STICK) return@on
            if (!player.hasPermission("dockyard.debug_stick")) return@on
            if (block.registryBlock.states.isEmpty()) return@on

            event.cancel()
            handleLeftClick(player, item, block)
        }

    }

    private fun getOrCreateLeftClickState(player: Player, item: ItemStack, block: Block, silent: Boolean): RegistryBlockState {
        val currentState = item.components.getOrNull(DebugStickItemComponent::class) ?: return handleLeftClick(player, item, block, silent)

        val currentName = currentState.data.getString(block.identifier)

        val next = if (currentName == null) {
            block.registryBlock.states.first()
        } else {
            val currentIndex = block.registryBlock.states.map { state -> state.name }.indexOf(currentName)
            block.registryBlock.states[currentIndex]
        }

        return next
    }

    private fun handleLeftClick(player: Player, item: ItemStack, block: Block, silent: Boolean = false): RegistryBlockState {
        var currentState = item.components.getOrNull(DebugStickItemComponent::class)

        if (currentState == null) {
            currentState = DebugStickItemComponent(NBTCompound.EMPTY)
        }

        var current: String? = null
        if (currentState.data.containsKey(block.identifier)) {
            current = currentState.data.getString(block.identifier)
        }

        val next = if (current == null) {
            block.registryBlock.states.first()
        } else {
            val currentIndex = block.registryBlock.states.map { state -> state.name }.indexOf(current)
            val nextIndex = (currentIndex + 1) % block.registryBlock.states.size
            block.registryBlock.states[nextIndex]
        }

        val map = currentState.data.modify { builder ->
            builder.put(block.identifier, next.name)
        }

        player.inventory[player.heldSlotIndex.value] = item.copy().withComponent(DebugStickItemComponent(map))
        player.inventory[player.heldSlotIndex.value] = item.copy().withComponent(DebugStickItemComponent(map))
        player.inventory.sendInventoryUpdate(player.heldSlotIndex.value)

        val currentValueOfSelectedState = block.blockStates[next.name]
        if (!silent) player.sendActionBar("selected \"${next.name}\" ($currentValueOfSelectedState)")
        return next
    }
}