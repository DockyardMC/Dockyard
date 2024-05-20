package io.github.dockyardmc.protocol

class UnknownPacketException(override val message: String): Exception() {

    init {
        this.stackTrace = arrayOf()
    }
}