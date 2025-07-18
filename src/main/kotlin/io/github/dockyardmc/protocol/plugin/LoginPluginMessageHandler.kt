package io.github.dockyardmc.protocol.plugin

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.login.ClientboundLoginPluginRequestPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class LoginPluginMessageHandler(val networkManager: PlayerNetworkManager) {
    companion object {
        val REQUEST_ID = AtomicInteger(0)
    }

    private val requestByMessage: MutableMap<Int, Request> = mutableMapOf()

    data class Request(val channel: String, val requestPayload: ByteBuf) {
        val future = CompletableFuture<Response>()
    }

    data class Response(val channel: String, val responsePayload: ByteBuf? = null)

    fun request(connection: ChannelHandlerContext, channel: String, requestPayload: ByteBuf): CompletableFuture<Response> {
        val request = Request(channel, requestPayload)
        val id = REQUEST_ID.getAndIncrement()
        requestByMessage[id] = request
        connection.sendPacket(ClientboundLoginPluginRequestPacket(id, channel, requestPayload), networkManager)
        return request.future
    }

    fun handleResponse(messageId: Int, responsePayload: ByteBuf?) {
        val request = requestByMessage.remove(messageId)
        if (request == null) {
            log("Received unexpected login plugin response with id $messageId of ${responsePayload?.readableBytes()} bytes", LogType.ERROR)
            return
        }

        try {
            val response = Response(request.channel, responsePayload)
            request.future.complete(response)
        } catch (exception: Exception) {
            log("Error handling login plugin response on channel ${request.channel}:", LogType.ERROR)
            log(exception)
        }
    }

}