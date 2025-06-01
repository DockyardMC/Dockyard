package io.github.dockyardmc.data

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.asRGBHash
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.scroll.CustomColor

object CRC32CHasher {

    // should have 0 probability to appear as CRC32C hash due to odd parity, we can use it as marker for inline values in maps
    const val INLINE: Int = 0x00000001

    private val KEY_COMPARATOR: Comparator<Map.Entry<Int, Int>> = java.util.Map.Entry.comparingByKey(Comparator.comparingLong { x: Int -> Integer.toUnsignedLong(x) })
    private val VALUE_COMPARATOR: Comparator<Map.Entry<Int, Int>> = java.util.Map.Entry.comparingByValue(Comparator.comparingLong { x: Int -> Integer.toUnsignedLong(x) })
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

    fun ofColor(color: CustomColor): Int {
        return ofInt(color.asRGBHash())
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

    fun ofRegistryEntry(entry: RegistryEntry): Int {
        return ofString(entry.getEntryIdentifier())
    }

    inline fun <reified T : Enum<T>> ofEnum(enum: T): Int {
        if(enum is DataComponentHashable) return enum.hashStruct().getHashed()
        val hashed = ofString(enum.name.lowercase())
        return hashed
    }

    fun ofEmptyList(): Int = EMPTY_LIST


    fun ofList(vararg int: Int): Int {
        return ofList(int.toList())
    }

    fun ofList(int: List<Int>): Int {
        val hasher = Hasher().putByte(TAG_LIST_START)
        int.forEach { number ->
            hasher.putIntBytes(number)
        }

        return hasher.putByte(TAG_LIST_END).hash()
    }

    @JvmName("ofListHashable")
    fun ofList(hashables: List<DataComponentHashable>): Int {
        return ofList(hashables.map { item -> item.hashStruct().getHashed() })
    }

    fun onEmptyMap(): Int {
        return EMPTY_MAP
    }

    fun of(unit: HashStruct.Builder.() -> Unit): HashStruct {
        val builder = HashStruct.Builder()
        unit.invoke(builder)
        return HashStruct(builder.fields)
    }

    fun ofMap(map: Map<Int, Int>, customHasher: Hasher? = null): Int {
        if (map.isEmpty()) return EMPTY_MAP
        val hasher = customHasher ?: Hasher()

        hasher.putByte(TAG_MAP_START)

        map.entries.stream().sorted(MAP_COMPARATOR).forEach { entry ->
            if(entry.value == EMPTY) return@forEach
            log("key: ${entry.key}", LogType.DEBUG)
            log("value: ${entry.value}", LogType.DEBUG)
            if (entry.key != INLINE) hasher.putIntBytes(entry.key)
            hasher.putIntBytes(entry.value)
        }

        hasher.putByte(TAG_MAP_END)
        return hasher.hash()
    }

    data class WrappedKey(val value: Int, val isInline: Boolean)

    @JvmName("ofPairsMap")
    fun ofMap(vararg pairs: Pair<Int, Int>): Int {
        return ofMap(pairs.toMap())
    }

    @JvmName("ofMapNoInline")
    fun ofMap(vararg pairs: Pair<String, Int>): Int {
        val newMap: Map<Int, Int> = pairs.toMap().mapKeys { key -> if (key.key.isEmpty()) INLINE else ofString(key.key) }
        return ofMap(newMap)
    }

    @JvmName("ofMapNoInlineaaaaaa")
    fun ofMap(map: Map<String, Int>): Int {
        val newMap: Map<Int, Int> = map.mapKeys { key -> if (key.key.isEmpty()) INLINE else ofString(key.key) }
        return ofMap(newMap)
    }

    @JvmName("ofMapInlineTest")
    fun ofMap(hasher: Hasher, vararg pairs: Pair<String, Int>): Int {
        return ofMap(pairs.toMap().mapKeys { key -> if (key.key.isEmpty()) INLINE else ofString(key.key) }, hasher)
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

