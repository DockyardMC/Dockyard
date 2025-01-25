package io.github.dockyardmc.common

fun <K, V> Map<K, V>.reversed(): Map<V, K> = this.entries.associate { (k, v) -> v to k }

fun <K, V> Map<K, V>.getOrThrow(key: K): V = this[key] ?: throw NoSuchElementException("Value with key $key is not present in this map!")