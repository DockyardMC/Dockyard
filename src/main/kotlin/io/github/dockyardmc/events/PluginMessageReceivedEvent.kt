package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("server receives plugin message from client (Custom payload packet)", false)
class PluginMessageReceivedEvent(var channel: String, var data: String): Event