package io.github.dockyardmc.noxesium.rules

import com.noxcrew.noxesium.api.protocol.rule.ServerRule
import io.netty.buffer.ByteBuf

abstract class NoxesiumServerRule<T : Any?>(val ruleIndex: Int, val defaultValue: T) : ServerRule<T, ByteBuf>() {

    private var value: T = defaultValue

    override fun getIndex(): Int {
        return ruleIndex
    }

    override fun getDefault(): T {
        return defaultValue
    }

    override fun getValue(): T {
        return value
    }

    override fun setValue(value: T) {
        this.value = value
    }

    override fun read(buffer: ByteBuf): T = throw UnsupportedOperationException("Cannot read a server-side server rule from a buffer")


}