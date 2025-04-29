package io.github.dockyardmc.item

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Transcoder
import io.github.dockyardmc.utils.uuidToIntArray
import java.util.*
import kotlin.reflect.KClass

object TranscoderCRC32C : Transcoder<TranscoderCRC32C.HashContainer<*>> {

    interface HashContainer<T> {
        fun getValue(): T
        fun add(field: String, value: Int)
    }

    data class HashContainerValue(var value: Int) : HashContainer<Int> {

        override fun getValue(): Int {
            return value
        }

        override fun add(field: String, value: Int) {
            this.value = value
        }
    }

    data class HashContainerMap(var map: MutableMap<Int, Int>) : HashContainer<MutableMap<Int, Int>> {

        override fun getValue(): MutableMap<Int, Int> {
            return map
        }

        override fun add(field: String, value: Int) {
            map[CRC32CHasher.ofString(field)] = value
        }
    }

    data class HashContainerList(var list: MutableList<Int>) : HashContainer<MutableList<Int>> {

        override fun getValue(): MutableList<Int> {
            return list
        }

        override fun add(field: String, value: Int) {
            list.add(value)
        }
    }

    private fun <T> writeOnly(): T {
        throw IllegalArgumentException("TranscoderCRC32C does not support reading")
    }

    override fun writeInt(format: HashContainer<*>, field: String, value: Int) {
        format.add(field, CRC32CHasher.ofInt(value))
    }

    override fun writeBoolean(format: HashContainer<*>, field: String, value: Boolean) {
        format.add(field, CRC32CHasher.ofBoolean(value))
    }

    override fun writeByte(format: HashContainer<*>, field: String, value: Byte) {
        format.add(field, CRC32CHasher.ofByte(value))
    }

    override fun writeByteArray(format: HashContainer<*>, field: String, value: ByteArray) {
        format.add(field, CRC32CHasher.ofByteArray(value))
    }

    override fun writeDouble(format: HashContainer<*>, field: String, value: Double) {
        format.add(field, CRC32CHasher.ofDouble(value))
    }

    override fun <E> writeEnum(kClass: KClass<*>, format: HashContainer<*>, field: String, value: E) {
        format.add(field, CRC32CHasher.ofInt((value as Enum<*>).ordinal))
    }

    override fun writeFloat(format: HashContainer<*>, field: String, value: Float) {
        format.add(field, CRC32CHasher.ofFloat(value))
    }

    override fun <D> writeList(format: HashContainer<*>, field: String, value: List<D>, codec: Codec<D>) {
        val container = HashContainerList(mutableListOf())

        value.forEach { element ->
            codec.writeTranscoded(TranscoderCRC32C, container, element, "")
        }

        format.add(field, CRC32CHasher.ofList(container.list))
    }

    override fun writeLong(format: HashContainer<*>, field: String, value: Long) {
        format.add(field, CRC32CHasher.ofLong(value))
    }

    override fun <K, V> writeMap(format: HashContainer<*>, field: String, value: Map<K, V>, keyCodec: Codec<K>, valueCodec: Codec<V>) {
        val hashedMap = mutableMapOf<Int, Int>()

        value.forEach { element ->
            val keyHashContainer = HashContainerValue(0)
            val valueHashContainer = HashContainerValue(0)
            keyCodec.writeTranscoded(TranscoderCRC32C, keyHashContainer, element.key, "")
            valueCodec.writeTranscoded(TranscoderCRC32C, valueHashContainer, element.value, "")

            val hashedKey = keyHashContainer.getValue()
            val hashedValue = valueHashContainer.getValue()
            hashedMap[hashedKey] = hashedValue
        }

        format.add(field, CRC32CHasher.ofMap(hashedMap))
    }

    override fun <D> writeOptional(format: HashContainer<*>, field: String, value: D?, codec: Codec<D>) {
        if (value == null) {
            format.add(field, CRC32CHasher.EMPTY)
        } else {
            val hashed = HashContainerValue(0)
            codec.writeTranscoded(TranscoderCRC32C, hashed, value, "")
            format.add(field, hashed.getValue())
        }
    }

    override fun writeString(format: HashContainer<*>, field: String, value: String) {
        format.add(field, CRC32CHasher.ofString(value))
    }

    override fun writeUUID(format: HashContainer<*>, field: String, value: UUID) {
        format.add(field, CRC32CHasher.ofIntArray(uuidToIntArray(value)))
    }

    override fun writeVarInt(format: HashContainer<*>, field: String, value: Int) {
        format.add(field, CRC32CHasher.ofInt(value))
    }

    override fun readBoolean(format: HashContainer<*>, field: String): Boolean {
        return writeOnly()
    }

    override fun readByte(format: HashContainer<*>, field: String): Byte {
        return writeOnly()
    }

    override fun readByteArray(format: HashContainer<*>, field: String): ByteArray {
        return writeOnly()
    }

    override fun readDouble(format: HashContainer<*>, field: String): Double {
        return writeOnly()
    }

    override fun <E> readEnum(kClass: KClass<*>, format: HashContainer<*>, field: String): E {
        return writeOnly()
    }

    override fun readFloat(format: HashContainer<*>, field: String): Float {
        return writeOnly()
    }

    override fun readInt(format: HashContainer<*>, field: String): Int {
        return writeOnly()
    }

    override fun <D> readList(format: HashContainer<*>, field: String, codec: Codec<D>): List<D> {
        return writeOnly()
    }

    override fun readLong(format: HashContainer<*>, field: String): Long {
        return writeOnly()
    }

    override fun <K, V> readMap(format: HashContainer<*>, field: String, keyCodec: Codec<K>, valueCodec: Codec<V>): Map<K, V> {
        return writeOnly()
    }

    override fun <D> readOptional(format: HashContainer<*>, field: String, codec: Codec<D>): D? {
        return writeOnly()
    }

    override fun readString(format: HashContainer<*>, field: String): String {
        return writeOnly()
    }

    override fun readUUID(format: HashContainer<*>, field: String): UUID {
        return writeOnly()
    }

    override fun readVarInt(format: HashContainer<*>, field: String): Int {
        return writeOnly()
    }
}