package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

@OptIn(ExperimentalSerializationApi::class)
object ItemRegistry : DataDrivenRegistry {

    override val identifier: String = "minecraft:item"

    var items: MutableMap<String, Item> = mutableMapOf()
    var protocolIdToItem: MutableMap<Int, Item> = mutableMapOf()

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<Item>>(stream)
        val protocolIdCounter = AtomicInteger()

        list.forEach {

            items[it.identifier] = it
            protocolIdToItem[protocolIdCounter.getAndIncrement()] = it
        }

        //TODO Figure default components out for future release, keeping this not implement for the time being so this update can move on
//        list.forEach {
//            val components = mutableListOf<ItemComponent>()
//            it.encodedComponents.forEach { component ->
//                val id = component.key
//                val buffer = Unpooled.copiedBuffer(component.value, Charset.defaultCharset())
//                val itemComponent = buffer.readComponent(id)
//                components.add(itemComponent)
//                log(itemComponent.toString())
//            }
//        }
    }

    override fun get(identifier: String): Item {
        return items[identifier] ?: throw RegistryException(identifier, items.size)
    }

    override fun getOrNull(identifier: String): Item? {
        return items[identifier]
    }

    override fun getByProtocolId(id: Int): Item {
        return protocolIdToItem[id] ?: throw RegistryException(id, items.size)
    }

    override fun getMap(): Map<String, Item> {
        return items
    }
}

@Serializable
data class Item(
    val identifier: String,
    val displayName: String,
    val maxStack: Int,
    val drinkingSound: String,
    val eatingSound: String,
    val canFitInsideContainers: Boolean,
    val isEnchantable: Boolean,
    val isStackable: Boolean,
    val isDamageable: Boolean,
    val isBlock: Boolean,
    val encodedComponents: MutableMap<Int, String>,
) : RegistryEntry {

    override fun getNbt(): NBTCompound? = null

    override fun getProtocolId(): Int {
        return ItemRegistry.protocolIdToItem.reversed()[this]
            ?: throw IllegalStateException("This item is not in the registry (how did you get hold of this object??)")
    }

    fun toItemStack(amount: Int = 1): ItemStack {
        return ItemStack(this, amount)
    }
}