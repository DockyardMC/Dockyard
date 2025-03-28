package io.github.dockyardmc.data

class Crc32Hash {
    companion object {
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
    }
}