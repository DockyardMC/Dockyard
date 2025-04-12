package io.github.dockyardmc.utils

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

// the map is BI?? 🏳️‍🌈🏳️‍🌈🏳️‍🌈🏳️‍🌈
class BiMap<K, V>() {
    private val keyToValue: Object2ObjectOpenHashMap<K, V> = Object2ObjectOpenHashMap<K, V>()
    private val valueToKey: Object2ObjectOpenHashMap<V, K> = Object2ObjectOpenHashMap<V, K>()

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

    fun put(key: K, value: V) {
        keyToValue[key] = value
        valueToKey[value] = key
    }

    fun removeByKey(key: K) {
        val value = keyToValue.getOrDefault(key, null) ?: return
        keyToValue.remove(key)
        valueToKey.remove(value)
    }

    fun removeByValue(value: V) {
        val key = valueToKey.getOrDefault(value, null) ?: return
        keyToValue.remove(key)
        valueToKey.remove(value)
    }
}