package io.github.dockyardmc.data

import io.github.dockyardmc.data.CRC32CHasher.ofMap

interface HashHolder {
    fun getHash(): Int
}

data class HashSingle(val holder: HashHolder) : HashHolder {

    override fun getHash(): Int {
        return holder.getHash()
    }
}

data class StaticHash(val hash: Int) : HashHolder {
    override fun getHash(): Int {
        return hash
    }
}

data class HashList(val holders: List<HashHolder>) : HashHolder {

    override fun getHash(): Int {
        return CRC32CHasher.ofList(holders.map { holder -> holder.getHash() })
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

        fun inline(struct: HashStruct) {
            fields.add(Field.Inline(struct))
        }
    }

    interface Field {
        val name: String

        data class Static(override val name: String, val hash: Int) : Field
        data class Inline(val struct: HashStruct) : Field {
            override val name: String = ""
        }

        data class Default<T>(override val name: String, val default: T, val current: T, val kFunction1: (T) -> Int) : Field {
            fun getHash(): Int {
                if (current == default) {
                    return CRC32CHasher.EMPTY
                }
                return kFunction1.invoke(current)
            }
        }
    }

    private fun getFieldsAsMapFromInline(): Map<String, Int> {
        val finalMap = mutableMapOf<String, Int>()
        fields.forEach { field ->
            when (field) {
                is Field.Inline -> throw IllegalArgumentException("Inline field cannot have struct containing another inline field.")
                is Field.Default<*> -> finalMap[field.name] = field.getHash()
                is Field.Static -> finalMap[field.name] = field.hash
            }
        }
        return finalMap
    }

    override fun getHash(): Int {
        val mapFields = mutableMapOf<String, Int>()
        this.fields.forEach { field ->
            when (field) {
                is Field.Static -> mapFields[field.name] = field.hash
                is Field.Inline -> mapFields.putAll(field.struct.getFieldsAsMapFromInline())
                is Field.Default<*> -> mapFields[field.name] = field.getHash()
            }
        }

        return ofMap(mapFields)
    }
}