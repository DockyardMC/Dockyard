package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
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

    }


    override fun get(identifier: String): Item {
        return items[identifier]
            ?: throw IllegalStateException("Registry entry with identifier $identifier was not found")
    }

    override fun getOrNull(identifier: String): Item? {
        return items[identifier]
    }

    override fun getByProtocolId(id: Int): Item {
        return protocolIdToItem[id] ?: throw IllegalStateException("Item with protocol id $id is not in the registry!")
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
            ?: throw IllegalStateException("This item is not in registry")
    }

}