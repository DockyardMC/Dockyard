package io.github.dockyardmc.protocol

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

object NetworkCompression {

    var compressionThreshold: Int = -1

    fun decompress(input: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(input)

        val outputStream = ByteArrayOutputStream()
        val output = ByteArray(1024)
        try {
            while(!inflater.finished()) {
                val decompressionSize = inflater.inflate(output)
                outputStream.write(output, 0, decompressionSize)
            }
        } catch (ex: Exception) {
            log("Data of the input is not valid compressed data", LogType.ERROR)
            log(ex)
        }

        return outputStream.toByteArray()
    }

    fun compress(input: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(input)
        deflater.finish()

        val outputStream = ByteArrayOutputStream()
        val output = ByteArray(1024)

        while(!deflater.finished()) {
            val compressedSize = deflater.deflate(output)
            outputStream.write(output, 0, compressedSize)
        }

        return outputStream.toByteArray()
    }
}