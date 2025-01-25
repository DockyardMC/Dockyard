package io.github.dockyardmc.spark

import io.github.dockyardmc.commands.CommandExecutor
import io.github.dockyardmc.scroll.serializers.JsonToComponentSerializer
import me.lucko.spark.common.command.sender.AbstractCommandSender
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import java.util.*

class SparkCommandSender(delegate: CommandExecutor) : AbstractCommandSender<CommandExecutor>(delegate) {

    override fun getName(): String {
        return if(delegate.isPlayer) delegate.player!!.username else "console"
    }

    override fun getUniqueId(): UUID? {
        return if(delegate.isPlayer) delegate.player!!.uuid else null
    }

    override fun sendMessage(component: Component) { // adventure component
        val json = component.children().forEach { child ->
            val out = JSONComponentSerializer.json().serialize(component)
            JsonToComponentSerializer.serialize(out)
        }
    }

    override fun hasPermission(permission: String): Boolean {
        return delegate.hasPermission(permission)
    }
}