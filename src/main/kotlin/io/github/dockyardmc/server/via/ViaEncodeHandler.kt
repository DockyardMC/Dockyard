package io.github.dockyardmc.server.via

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.exception.CancelCodecException
import com.viaversion.viaversion.exception.CancelEncoderException
import com.viaversion.viaversion.util.ByteBufUtil
import com.viaversion.viaversion.util.PipelineUtil
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.handler.codec.MessageToMessageEncoder
import java.lang.Exception

class ViaEncodeHandler(val user: UserConnection): MessageToMessageEncoder<ByteBuf>() {

    override fun encode(connection: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        if(!user.checkClientboundPacket()) throw CancelEncoderException.generate(null)

        if(!user.shouldTransformPacket()) {
            out.add(buffer.retain())
            return
        }

        val transformedBuffer = ByteBufUtil.copy(connection.alloc(), buffer)
        try {
            user.transformOutgoing(transformedBuffer, CancelEncoderException::generate)
        } catch (ex: Exception) {
            transformedBuffer.release()
        }
    }

    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        try {
            super.write(ctx, msg, promise)
        } catch (exception: Exception) {
            if (!PipelineUtil.containsCause(exception, CancelCodecException::class.java)) {
                throw exception
            }
            promise.setSuccess()
        }
    }
}