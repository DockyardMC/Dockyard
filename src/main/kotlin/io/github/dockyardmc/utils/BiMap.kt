package io.github.dockyardmc.utils

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

// the map is BI?? 🏳️‍🌈🏳️‍🌈🏳️‍🌈🏳️‍🌈
open class BiMap<K, V>() {
    protected val keyToValue: Object2ObjectOpenHashMap<K, V> = Object2ObjectOpenHashMap<K, V>()
    protected val valueToKey: Object2ObjectOpenHashMap<V, K> = Object2ObjectOpenHashMap<V, K>()

    val size get() = keyToValue.size

    fun keyToValue(): Map<K, V> {
        return keyToValue.toMap()
    }

    fun valueToKey(): Map<V, K> {
        return valueToKey.toMap()
    }

    fun getByKeyOrNull(key: K): V? {
        return keyToValue.getOrDefault(key, null)
    }

    fun getByValueOrNull(value: V): K? {
        return valueToKey.getOrDefault(value, null)
    }

    fun getByKey(key: K): V {
        return getByKeyOrNull(key) ?: throw NoSuchElementException()
    }

    fun getByValue(value: V): K {
        return getByValueOrNull(value) ?: throw NoSuchElementException()
    }

    fun toMutableBiMap(): MutableBiMap<K, V> {
        return this as MutableBiMap<K, V>
    }
}