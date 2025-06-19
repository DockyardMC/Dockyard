package io.github.dockyardmc.data

import cz.lukynka.prettylog.log
import io.github.dockyardmc.data.CRC32CHasher.ofMap
import kotlin.reflect.KFunction1

interface HashHolder {

    fun getHashed(): Int
}

data class HashSingle(val holder: HashHolder) : HashHolder {

    override fun getHashed(): Int {
        return holder.getHashed()
    }
}

data class StaticHash(val hash: Int) : HashHolder {

    override fun getHashed(): Int {
        return hash
    }
}

data class HashList(val holders: List<HashHolder>) : HashHolder {

    override fun getHashed(): Int {
        return CRC32CHasher.ofList(holders.map { holder -> holder.getHashed() })
    }

}

data class HashStruct(val fields: List<Field>) : HashHolder {

    constructor(vararg field: Field) : this(field.toList())

    companion object {
        val EMPTY = HashStruct(emptyList())
    }

    class Builder {
        val fields: MutableList<Field> = mutableListOf()

        fun static(name: String, hash: Int) {
            fields.add(Field.Static(name, hash))
        }

        fun <T> default(name: String, default: T, current: T, kFunction1: (T) -> Int) {
            fields.add(Field.Default<T>(name, default, current, kFunction1))
        }

        fun <T> defaultStruct(name: String, default: T, current: T, kFunction1: (T) -> HashHolder) {
            fields.add(Field.DefaultStruct<T>(name, default, current, kFunction1))
        }

        fun <T> optional(name: String, value: T?, kFunction1: (T) -> Int) {
            fields.add(Field.Optional<T>(name, value, kFunction1))
        }

        fun <T> optionalStruct(name: String, value: T?, kFunction1: (T) -> HashHolder) {
            fields.add(Field.OptionalStruct<T>(name, value, kFunction1))
        }

        fun <T> list(name: String, values: Collection<T>, kFunction: (T) -> Int) {
            fields.add(Field.List<T>(name, values, kFunction))
        }

        fun <T> structList(name: String, values: Collection<T>, kFunction: (T) -> HashHolder) {
            fields.add(Field.StructList<T>(name, values, kFunction))
        }

        fun <T> defaultList(name: String, default: Collection<T>, values: Collection<T>, kFunction: (T) -> Int) {
            fields.add(Field.DefaultList<T>(name, default, values, kFunction))
        }

        fun <T> defaultStructList(name: String, default: Collection<T>, values: Collection<T>, struct: KFunction1<T, HashHolder>) {
            fields.add(Field.DefaultStructList<T>(name, default, values, struct))
        }

        fun <T> optionalList(name: String, values: Collection<T>?, kFunction: (T) -> Int) {
            fields.add(Field.OptionalList<T>(name, values, kFunction))
        }

        fun inline(struct: HashStruct) {
            fields.add(Field.Inline(struct))
        }

        fun <T> inline(value: T, kFunction1: (T) -> HashHolder) {
            fields.add(Field.InlineValue(value, kFunction1))
        }
    }

    interface Field {
        val name: String

        data class Static(override val name: String, val hash: Int) : Field

        data class Optional<T>(override val name: String, val value: T?, val kFunction1: (T) -> Int) : Field {
            fun getHash(): Int? {
                return if (value == null) null else kFunction1.invoke(value)
            }
        }

        data class OptionalStruct<T>(override val name: String, val value: T?, val kFunction1: (T) -> HashHolder) : Field {
            fun getHash(): Int? {
                return if (value == null) null else kFunction1.invoke(value).getHashed()
            }
        }

        data class Inline(val struct: HashStruct) : Field {
            override val name: String = ""
        }

        data class InlineValue<T>(val value: T, val kFunction1: (T) -> HashHolder) : Field {
            override val name: String = ""

            fun getFields(): Map<String, Int> {
                return (kFunction1.invoke(value) as HashStruct).getFieldsAsMapFromInline()
            }
        }

        data class Default<T>(override val name: String, val default: T, val current: T, val kFunction1: (T) -> Int) : Field {
            fun getHash(): Int {
                if (current == default) {
                    return CRC32CHasher.EMPTY
                }
                return kFunction1.invoke(current)
            }
        }

        data class DefaultStruct<T>(override val name: String, val default: T, val current: T, val kFunction1: (T) -> HashHolder) : Field {
            fun getHash(): Int {
                if (current == default) {
                    return CRC32CHasher.EMPTY
                }
                return kFunction1.invoke(current).getHashed()
            }
        }

        open class List<T>(override val name: String, val values: Collection<T>, val kFunction1: (T) -> Int) : Field {
            open fun getHash(): Int {
                return CRC32CHasher.ofList(values.map { value -> kFunction1.invoke(value) })
            }
        }

        open class StructList<T>(override val name: String, val values: Collection<T>, val kFunction1: (T) -> HashHolder) : Field {
            open fun getHash(): Int {
                return CRC32CHasher.ofList(values.map { value -> kFunction1.invoke(value).getHashed() })
            }
        }

        data class DefaultList<T>(override val name: String, val default: Collection<T>, val current: Collection<T>, val kFunction1: (T) -> Int) : Field {

            fun getHash(): Int {
                if (current == default) {
                    return CRC32CHasher.EMPTY
                }
                return CRC32CHasher.ofList(current.map { value -> kFunction1.invoke(value) })
            }
        }

        data class DefaultStructList<T>(override val name: String, val default: Collection<T>, val current: Collection<T>, val struct: KFunction1<T, HashHolder>) : Field {

            fun getHash(): Int {
                if (current == default) {
                    return CRC32CHasher.EMPTY
                }
                val hashedItems = mutableListOf<Int>()
                current.forEach { item ->
                    hashedItems.add(struct.invoke(item).getHashed())
                }
                return CRC32CHasher.ofList()
            }
        }

        data class OptionalList<T>(override val name: String, val values: Collection<T>?, val kFunction1: (T) -> Int) : Field {
            fun getHash(): Int {
                return if (values == null) CRC32CHasher.EMPTY else CRC32CHasher.ofList(values.map { value -> kFunction1.invoke(value) })
            }
        }

    }

    private fun getFieldsAsMapFromInline(): Map<String, Int> {
        val finalMap = mutableMapOf<String, Int>()
        fields.forEach { field ->
            when (field) {
                is Field.Inline -> throw IllegalArgumentException("Inline field cannot have struct containing another inline field.")
                is Field.InlineValue<*> -> throw IllegalArgumentException("Inline field cannot have struct containing another inline field.")
                is Field.Default<*> -> finalMap[field.name] = field.getHash()
                is Field.DefaultStruct<*> -> finalMap[field.name] = field.getHash()
                is Field.Static -> finalMap[field.name] = field.hash
                is Field.Optional<*> -> if (field.getHash() != null) finalMap[field.name] = field.getHash()!!
                is Field.OptionalStruct<*> -> if (field.getHash() != null) finalMap[field.name] = field.getHash()!!
                is Field.List<*> -> finalMap[field.name] = field.getHash()
                is Field.StructList<*> -> finalMap[field.name] = field.getHash()
                is Field.OptionalList<*> -> finalMap[field.name] = field.getHash()
                is Field.DefaultList<*> -> finalMap[field.name] = field.getHash()
                is Field.DefaultStructList<*> -> finalMap[field.name] = field.getHash()
            }
        }
        return finalMap
    }

    override fun getHashed(): Int {
        val mapFields = mutableMapOf<String, Int>()
        this.fields.forEach { field ->
            when (field) {
                is Field.Static -> mapFields[field.name] = field.hash
                is Field.Inline -> mapFields.putAll(field.struct.getFieldsAsMapFromInline())
                is Field.InlineValue<*> -> mapFields.putAll(field.getFields())
                is Field.Default<*> -> mapFields[field.name] = field.getHash()
                is Field.DefaultStruct<*> -> mapFields[field.name] = field.getHash()
                is Field.Optional<*> -> if (field.getHash() != null) mapFields[field.name] = field.getHash()!!
                is Field.OptionalStruct<*> -> if (field.getHash() != null) mapFields[field.name] = field.getHash()!!
                is Field.List<*> -> mapFields[field.name] = field.getHash()
                is Field.StructList<*> -> mapFields[field.name] = field.getHash()
                is Field.OptionalList<*> -> mapFields[field.name] = field.getHash()
                is Field.DefaultList<*> -> mapFields[field.name] = field.getHash()
                is Field.DefaultStructList<*> -> mapFields[field.name] = field.getHash()
            }
        }

        return ofMap(mapFields)
    }
}