package io.github.dockyardmc.data

import io.github.dockyardmc.item.TranscoderCRC32C

object DataComponentHasher {
    fun hash(component: DataComponent): Int {
        val format: TranscoderCRC32C.HashContainer<*> = if (component.isSingleField) {
            TranscoderCRC32C.HashContainerValue()
        } else {
            TranscoderCRC32C.HashContainerMap()
        }

//        (component.getHashCodec() as Codec<DataComponent>).writeTranscoded(TranscoderCRC32C, format, component, "")

        return when (format) {
            is TranscoderCRC32C.HashContainerMap -> CRC32CHasher.ofMap(format.getValue())
            is TranscoderCRC32C.HashContainerValue -> format.getValue()
            else -> throw IllegalArgumentException("not supported")
        }
    }
}