package io.github.dockyardmc.server.via

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.exception.CancelCodecException
import com.viaversion.viaversion.exception.CancelDecoderException
import com.viaversion.viaversion.util.ByteBufUtil
import com.viaversion.viaversion.util.PipelineUtil
import cz.lukynka.prettylog.log
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import java.lang.Exception

@ChannelHandler.Sharable
class ViaDecodeHandler(val user: UserConnection): MessageToMessageDecoder<ByteBuf>() {

    override fun decode(connection: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        if(!user.checkServerboundPacket()) throw CancelDecoderException.generate(null)
        if(!user.shouldTransformPacket()) {
            out.add(buffer.retain())
            return
        }

        val transformedBuffer = ByteBufUtil.copy(connection.alloc(), buffer)
        try {
            user.transformIncoming(transformedBuffer, CancelDecoderException::generate)
            out.add(transformedBuffer.retain())
        } finally {
            transformedBuffer.release()
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        try{
            super.channelRead(ctx, msg)
        } catch (exception: Exception) {
            if(!PipelineUtil.containsCause(exception, CancelCodecException::class.java)) {
                log(exception)
                throw exception
            }
        }
    }
}