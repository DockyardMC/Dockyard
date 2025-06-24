package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf

@JvmName("writeMap1")
inline fun <K, V> ByteBuf.writeMap(map: Map<K, V>, writeKey: (ByteBuf, K) -> Unit, writeValue: (ByteBuf, V) -> Unit) {
    this.writeVarInt(map.size)
    map.forEach { (key, value) ->
        writeKey.invoke(this, key)
        writeValue.invoke(this, value)
    }
}

fun <K, V> ByteBuf.writeRawMap(map: Map<K, V>, writeKey: (ByteBuf, K) -> Unit, writeValue: (ByteBuf, V) -> Unit) {
    map.forEach { (key, value) ->
        writeKey.invoke(this, key)
        writeValue.invoke(this, value)
    }
}

@JvmName("writeMap2")
inline fun <K, V> ByteBuf.writeMap(map: Map<K, V>, writeKey: (ByteBuf, K) -> ByteBuf, writeValue: (V, ByteBuf) -> Unit) {
    this.writeVarInt(map.size)
    map.forEach { (key, value) ->
        writeKey.invoke(this, key)
        writeValue.invoke(value, this)
    }
}

inline fun <K, V> ByteBuf.readMap(readKey: (ByteBuf) -> K, readValue: (ByteBuf) -> V): Map<K, V> {
    val map = mutableMapOf<K, V>()

    repeat(readVarInt()) {
        val key = readKey.invoke(this)
        val value = readValue.invoke(this)
        map[key] = value
    }
    return map
}