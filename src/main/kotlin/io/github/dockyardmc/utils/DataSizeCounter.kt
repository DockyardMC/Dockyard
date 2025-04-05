package io.github.dockyardmc.utils

import io.github.dockyardmc.extentions.truncate

class DataSizeCounter(initialValue: Long = 0) {
    private var bytes: Long = initialValue

    fun add(value: Int, type: Type) {
        bytes += value.toLong() * type.mult
    }

    fun remove(value: Int, type: Type) {
        bytes -= value.toLong() * type.mult
    }

    fun getSize(type: Type): Double {
        return (bytes.toDouble() / type.mult.toDouble()).truncate(2).toDouble()
    }

    fun reset() {
        bytes = 0
    }

    enum class Type(val mult: Long) {
        BYTE(1L),
        KILOBYTE(1024L),
        MEGABYTE(KILOBYTE.mult * 1024L),
        GIGABYTE(MEGABYTE.mult * 1024L),
        TERABYTE(GIGABYTE.mult * 1024L),
    }
}