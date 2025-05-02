package io.github.dockyardmc.world.block.handlers

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.world.Weather
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block

interface BlockHandler {

    fun onPlace(
        player: Player,
        heldItem: ItemStack,
        block: Block,
        face: Direction,
        location: Location,
        clickedBlock: Location,
        cursor: Vector3f
    ): Block? {
        return block
    }

    fun onDestroy(
        block: Block,
        world: World,
        location: Location
    ) {
        // Nothing by default
    }

    fun onUse(
        player: Player,
        heldItem: ItemStack,
        block: Block,
        face: Direction,
        location: Location,
        clickedBlock: Location,
        cursor: Vector3f
    ): Boolean {
        // Nothing by default
        return false
    }

    fun onAttack(
        player: Player,
        location: Location,
        block: Block,
        face: Direction,
    ) {
        // Nothing by default
    }

    fun onUpdateByNeighbour(block: Block, world: World, location: Location, neighbour: Block, neighbourLocation: Location) {
        // Nothing by default
    }

    fun onWeatherChange(block: Block, world: World, location: Location, change: Bindable.ValueChangedEvent<Weather>, isOccluded: Boolean) {
        // Nothing by default
    }

    fun onPowerOnByRedstone(block: Block, world: World, location: Location, power: Int) {
        // Nothing by default
    }

    fun onPowerOffByRedstone(block: Block, world: World, location: Location, power: Int) {
        // Nothing by default
    }

}