package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.protocol.item.ItemStack
import io.github.dockyardmc.protocol.types.ItemComponent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jglrxavpok.hephaistos.nbt.NBTCompound

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
    var defaultComponents: List<ItemComponent>? = null
) : RegistryEntry() {

    override fun getNbt(): NBTCompound? = null

    override fun getIdentifier(): String {
        return identifier
    }

//    override fun getProtocolId(): Int {
//        return ItemRegistry.protocolIdToItem.reversed()[this]
//            ?: throw IllegalStateException("This item is not in the registry (how did you get hold of this object??)")
//    }

    fun toItemStack(amount: Int = 1): ItemStack {
        return ItemStack(this, amount)
    }
}