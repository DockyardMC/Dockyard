package io.github.dockyardmc.codec

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.tide.codec.Codec
import io.github.dockyardmc.tide.stream.StreamCodec
import io.github.dockyardmc.tide.transcoder.Transcoder
import io.netty.buffer.ByteBuf
import kotlin.time.Duration

object DurationCodec {

    val STREAM_CODEC_INT_TICKS = object : StreamCodec<Duration> {

        override fun write(buffer: ByteBuf, value: Duration) {
            buffer.writeVarInt(value.inWholeMinecraftTicks)
        }

        override fun read(buffer: ByteBuf): Duration {
            return buffer.readVarInt().ticks
        }
    }

    val CODEC_INT_TICKS = object : Codec<Duration> {

        override fun <D> encode(transcoder: Transcoder<D>, value: Duration): D {
            return transcoder.encodeInt(value.inWholeMinecraftTicks)
        }

        override fun <D> decode(transcoder: Transcoder<D>, value: D): Duration {
            return transcoder.decodeInt(value).ticks

        }
    }

}