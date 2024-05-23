package io.github.dockyardmc.extentions

fun <K, V> Map<K, V>.reversed(): Map<V, K> {
    return this.entries.associate { (k, v) -> v to k }
}