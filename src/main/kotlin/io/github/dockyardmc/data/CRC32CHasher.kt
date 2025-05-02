package io.github.dockyardmc.data

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log

object CRC32CHasher {
    private val KEY_COMPARATOR: Comparator<Map.Entry<Int, Int>> = java.util.Map.Entry.comparingByKey(Comparator.comparingLong { x: Int? -> Integer.toUnsignedLong(x!!) })
    private val VALUE_COMPARATOR: Comparator<Map.Entry<Int, Int>> = java.util.Map.Entry.comparingByValue(Comparator.comparingLong { x: Int? -> Integer.toUnsignedLong(x!!) })
    private val MAP_COMPARATOR: Comparator<Map.Entry<Int, Int>> = KEY_COMPARATOR.thenComparing(VALUE_COMPARATOR)

    const val TAG_EMPTY: Byte = 1
    const val TAG_MAP_START: Byte = 2
    const val TAG_MAP_END: Byte = 3
    const val TAG_LIST_START: Byte = 4
    const val TAG_LIST_END: Byte = 5
    const val TAG_BYTE: Byte = 6
    const val TAG_SHORT: Byte = 7
    const val TAG_INT: Byte = 8
    const val TAG_LONG: Byte = 9
    const val TAG_FLOAT: Byte = 10
    const val TAG_DOUBLE: Byte = 11
    const val TAG_STRING: Byte = 12
    const val TAG_BOOLEAN: Byte = 13
    const val TAG_BYTE_ARRAY_START: Byte = 14
    const val TAG_BYTE_ARRAY_END: Byte = 15
    const val TAG_INT_ARRAY_START: Byte = 16
    const val TAG_INT_ARRAY_END: Byte = 17
    const val TAG_LONG_ARRAY_START: Byte = 18
    const val TAG_LONG_ARRAY_END: Byte = 19

    val EMPTY = Hasher().putByte(TAG_EMPTY).hash()
    val EMPTY_MAP = Hasher().putByte(TAG_MAP_START).putByte(TAG_MAP_END).hash()
    val EMPTY_LIST = Hasher().putByte(TAG_LIST_START).putByte(TAG_LIST_END).hash()
    val FALSE = Hasher().putByte(TAG_BOOLEAN).putByte(0).hash()
    val TRUE = Hasher().putByte(TAG_BOOLEAN).putByte(1).hash()

    fun ofBoolean(boolean: Boolean): Int {
        return if (boolean) TRUE else FALSE
    }

    fun ofByte(byte: Byte): Int {
        return Hasher().putByte(TAG_BYTE).putByte(byte).hash()
    }

    fun ofShort(short: Short): Int {
        return Hasher().putByte(TAG_SHORT).putShort(short).hash()
    }

    fun ofInt(int: Int): Int {
        val hashed = Hasher().putByte(TAG_INT).putInt(int).hash()
        log("Hashed int $hashed", LogType.TRACE)
        return hashed
    }

    fun ofLong(long: Long): Int {
        return Hasher().putByte(TAG_LONG).putLong(long).hash()
    }

    fun ofFloat(float: Float): Int {
        return Hasher().putByte(TAG_FLOAT).putFloat(float).hash()
    }

    fun ofDouble(double: Double): Int {
        return Hasher().putByte(TAG_DOUBLE).putDouble(double).hash()
    }

    fun ofString(string: String): Int {
        val hashed = Hasher().putByte(TAG_STRING)
            .putInt(string.length)
            .putChars(string)
            .hash()
        log("Hashed string '$string' $hashed", LogType.TRACE)
        return hashed
    }

    fun ofEmptyList(): Int = EMPTY_LIST


    fun ofList(vararg int: Int): Int {
        return ofList(int.toList())
    }

    fun ofList(int: List<Int>): Int {
        val hasher = Hasher().putByte(TAG_LIST_START)
        int.forEach { number ->
            hasher.putInt(number)
        }

        return hasher.putByte(TAG_LIST_END).hash()
    }

    fun onEmptyMap(): Int {
        return EMPTY_MAP
    }

    fun ofMap(map: Map<Int, Int>): Int {
        if (map.isEmpty()) return EMPTY_MAP
        val hasher = Hasher().putByte(TAG_MAP_START)
        map.entries.stream().sorted(MAP_COMPARATOR).forEach { entry ->
            hasher.putIntBytes(entry.key)
            hasher.putIntBytes(entry.value)
        }

        return hasher.putByte(TAG_MAP_END).hash()
    }

    fun ofByteArray(byteArray: ByteArray): Int {
        return Hasher().putByte(TAG_BYTE_ARRAY_START).putBytes(byteArray).putByte(TAG_BYTE_ARRAY_END).hash()
    }

    fun ofIntArray(intArray: IntArray): Int {
        val hasher = Hasher().putByte(TAG_INT_ARRAY_START)
        intArray.forEach { int ->
            hasher.putInt(int)
        }
        return hasher.putByte(TAG_INT_ARRAY_END).hash()
    }

    fun ofLongArray(longArray: LongArray): Int {
        val hasher = Hasher().putByte(TAG_LONG_ARRAY_START)
        longArray.forEach { long ->
            hasher.putLong(long)
        }
        return hasher.putByte(TAG_LONG_ARRAY_END).hash()
    }
}