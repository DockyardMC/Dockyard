package io.github.dockyardmc.ui.examples

import cz.lukynka.prettylog.log
import io.github.dockyardmc.item.itemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.ui.*
import io.github.dockyardmc.utils.randomInt

class ExampleMinesweeperScreen(player: Player, val bombs: Int) : DrawableContainerScreen(player) {

    override var name: String = "<black><bold>Minesweeper <dark_gray>[<dark_red>$bombs bombs<dark_gray>]"
    override var rows: Int = 6

    val mines = mutableMapOf<Int, Boolean>()

    init {
        populateMines()
        for (i in 0 until 54) {
            val vec2 = getVector2FromSlotIndex(i)
            slots[vec2.x, vec2.y] = hiddenTile(i)
        }
    }

    fun populateMines() {
        for (i in 0 until bombs) {
            var index = randomInt(0, 53)
            while (mines[index] != null) {
                index = randomInt(0, 53)
            }
            mines[index] = true
        }
    }

    private fun hiddenTile(index: Int): DrawableItemStack {
        return drawableItemStack {
            withItem {
                withMaterial(Items.BLACK_STAINED_GLASS_PANE)
                withDisplayName("<aqua>?????")
                addLore("")
                addLore("<gray>Left Click: <yellow>Reveal Tile")
                addLore("<gray>Right Click: <yellow>Flag a Mine")
                addLore("")
            }
            onClick { player, drawableClickType ->
                when (drawableClickType) {
                    DrawableClickType.LEFT_CLICK -> {
                        val vec2 = getVector2FromSlotIndex(index)
                        val isMine = mines[index] != null
                        if (isMine) {
                            gameOver()
                            return@onClick
                        }

                        val numMines = getNumberOfMines(index)
                        val item: Pair<Item, String> = when (numMines) {
                            1 -> Items.BLUE_STAINED_GLASS_PANE to "<blue>"
                            2 -> Items.GREEN_STAINED_GLASS_PANE to "<green>"
                            3 -> Items.YELLOW_STAINED_GLASS_PANE to "<yellow>"
                            4 -> Items.ORANGE_STAINED_GLASS_PANE to "<orange>"
                            else -> Items.RED_STAINED_GLASS_PANE to "<red>"
                        }

                        log("$item")

                        slots[vec2.x, vec2.y] = drawableItemStack {
                            withItem {
                                withMaterial(item.first)
                                withDisplayName("${item.second}<u>$numMines of mines")
                                withAmount(numMines)
                            }
                        }
                    }

                    DrawableClickType.RIGHT_CLICK -> {
                        val vec2 = getVector2FromSlotIndex(index)
                        slots[vec2.x, vec2.y] = Items.FLINT_AND_STEEL.toDrawable()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun gameOver() {
        player.playSound(Sounds.ENTITY_GENERIC_EXPLODE)
        fill(0 to 0, 8 to 5, Items.RED_STAINED_GLASS_PANE.toDrawable())
        (0 until 54)
            .asSequence()
            .filter { mines[it] != null }
            .map { getVector2FromSlotIndex(it) }
            .forEach {
                slots[it.x, it.y] = drawableItemStack {
                    withItem(itemStack {
                        withMaterial(Items.TNT)
                        withDisplayName("<red><bold>BOMB")
                    })
                }
            }
    }

    private fun getNumberOfMines(centerIndex: Int): Int {
        val vec2 = getVector2FromSlotIndex(centerIndex)
        val gridWidth = 9
        val gridHeight = 6

        var mineCount = 0
        for (xOffset in -1..1) {
            for (yOffset in -1..1) {
                if (xOffset == 0 && yOffset == 0) continue
                val newX = vec2.x + xOffset
                val newY = vec2.y + yOffset
                if (newX in 0..<gridWidth && newY >= 0 && newY < gridHeight) {
                    val neighborIndex = getSlotIndexFromVector2(newX, newY)
                    if (mines[neighborIndex] != null) {
                        mineCount++
                    }
                }
            }
        }
        return mineCount
    }
}