package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

@JvmName("writeMap1")
fun <K, V> ByteBuf.writeMap(map: Map<K, V>, writeKey: KFunction2<ByteBuf, K, Unit>, writeValue: KFunction2<ByteBuf, V, Unit>) {
    this.writeVarInt(map.size)
    map.forEach { (key, value) ->
        writeKey.invoke(this, key)
        writeValue.invoke(this, value)
    }
}

@JvmName("writeMap2")
fun <K, V> ByteBuf.writeMap(map: Map<K, V>, writeKey: KFunction2<ByteBuf, K, ByteBuf>, writeValue: KFunction2<V, ByteBuf, Unit>) {
    this.writeVarInt(map.size)
    map.forEach { (key, value) ->
        writeKey.invoke(this, key)
        writeValue.invoke(value, this)
    }
}

@JvmName("writeMap3")
fun <K, V> ByteBuf.writeMap(map: Map<K, V>, writeKey: KFunction2<ByteBuf, K, ByteBuf>, writeValue: KFunction2<ByteBuf, V, ByteBuf>) {
    this.writeVarInt(map.size)
    map.forEach { (key, value) ->
        writeKey.invoke(this, key)
        writeValue.invoke(this, value)
    }
}

fun <K, V> ByteBuf.readMap(readKey: KFunction1<ByteBuf, K>, readValue: KFunction1<ByteBuf, V>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    val size = this.readVarInt()
    for (i in 0 until size) {
        val key = readKey.invoke(this)
        val value = readValue.invoke(this)
        map[key] = value
    }
    return map
}