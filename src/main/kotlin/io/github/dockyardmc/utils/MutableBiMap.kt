package io.github.dockyardmc.utils

class MutableBiMap<K, V> : BiMap<K, V>() {
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

    fun toBiMap(): BiMap<K, V> {
        return this
    }
}