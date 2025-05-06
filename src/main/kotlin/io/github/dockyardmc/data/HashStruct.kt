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

data class StaticHash(val hash: Int): HashHolder {
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
    }

    private fun getFieldsAsMapFromInline(): Map<String, Int> {
        val finalMap = mutableMapOf<String, Int>()
        fields.forEach { field ->
            if (field is Field.Inline) throw IllegalArgumentException("Inline field cannot have struct containing another inline field.")
            val static = field as Field.Static
            finalMap[static.name] = static.hash
        }
        return finalMap
    }

    override fun getHash(): Int {
        val mapFields = mutableMapOf<String, Int>()
        this.fields.forEach { field ->
            when (field) {
                is Field.Static -> mapFields[field.name] = field.hash
                is Field.Inline -> mapFields.putAll(field.struct.getFieldsAsMapFromInline())
            }
        }

        return ofMap(mapFields)
    }
}