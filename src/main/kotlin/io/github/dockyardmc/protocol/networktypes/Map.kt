package io.github.dockyardmc.protocol.networktypes

import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf
import kotlin.reflect.KFunction2

fun <K, V> ByteBuf.writeMap(map: Map<K, V>, writeKey: KFunction2<ByteBuf, K, Unit>, writeValue: KFunction2<ByteBuf, V, Unit>) {
    this.writeVarInt(map.size)

}