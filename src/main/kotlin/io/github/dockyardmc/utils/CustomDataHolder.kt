package io.github.dockyardmc.utils

class CustomDataHolder {
    val dataStore = mutableMapOf<String, Any>()

    fun <T : Any> add(key: String, value: T) {
        dataStore[key] = value
    }

    fun remove(key: String) {
        dataStore.remove(key)
    }

    inline operator fun <reified T> get(key: String): T? {
        if (!dataStore.containsKey(key)) throw IllegalArgumentException("Value for key $key not found in data holder")
        val value = dataStore[key]
        if (value !is T) throw IllegalArgumentException("Value for key $key is not of type ${T::class.simpleName}")
        return value
    }

    operator fun contains(key: String): Boolean {
        return dataStore.containsKey(key)
    }
}