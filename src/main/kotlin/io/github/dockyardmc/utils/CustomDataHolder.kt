package io.github.dockyardmc.utils

class CustomDataHolder {
    val dataStore = mutableMapOf<String, Any>()

    operator fun <T : Any> set(key: String, value: T) {
        dataStore[key] = value
    }

    fun remove(key: String) {
        dataStore.remove(key)
    }

    inline fun <reified T> getOrNull(key: String): T? {
        if (!dataStore.containsKey(key)) return null
        val value = dataStore[key]
        require(value is T) { "Value for key $key is not of type ${T::class.simpleName}" }
        return value
    }

    inline operator fun <reified T> get(key: String): T? {
        require(dataStore.containsKey(key)) { "Value for key $key not found in data holder" }

        val value = dataStore[key]
        require(value is T) { "Value for key $key is not of type ${T::class.simpleName}" }

        return value
    }

    operator fun contains(key: String): Boolean {
        return dataStore.containsKey(key)
    }
}