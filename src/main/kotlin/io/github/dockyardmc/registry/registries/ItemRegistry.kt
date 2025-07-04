package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

object ItemRegistry : DataDrivenRegistry<Item>() {
    override val identifier: String = "minecraft:item"
}

@Serializable
data class Item(
    val identifier: String,
    val displayName: String,
    val maxStack: Int,
    val consumeSound: String,
    val canFitInsideContainers: Boolean,
    val isEnchantable: Boolean,
    val isStackable: Boolean,
    val isDamageable: Boolean,
    val isBlock: Boolean,
    @Transient
    var defaultComponents: List<DataComponent>? = null
) : RegistryEntry {

    override fun getProtocolId(): Int {
        return ItemRegistry.getProtocolIdByEntry(this)
    }

    fun toItemStack(amount: Int = 1): ItemStack {
        return ItemStack(this, amount)
    }

    override fun getEntryIdentifier(): String {
        return identifier
    }

}