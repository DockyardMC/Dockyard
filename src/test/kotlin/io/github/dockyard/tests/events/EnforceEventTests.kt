package io.github.dockyard.tests.events

import io.github.dockyard.tests.EnforceTestsAbstract
import io.github.dockyardmc.events.*
import io.github.dockyardmc.events.noxesium.*

class EnforceEventTests : EnforceTestsAbstract() {
    override val testsPackage: String = "io.github.dockyard.tests.events"
    override val prodPackage: String = "io.github.dockyardmc.events"
    override val ignoredClasses: List<Class<*>> = listOf(
        PlayerSendFeatureFlagsEvent::class.java,
        PlayerLeaveEvent::class.java,
        ServerFinishLoadEvent::class.java,
        PlayerClientSettingsEvent::class.java,
        PlayerSpawnEvent::class.java,
        PacketSentEvent::class.java,
        EntityNavigatorPickOffsetEvent::class.java,
        RegisterPluginChannelsEvent::class.java,
        ServerStartEvent::class.java,
        UnregisterPluginChannelsEvent::class.java,
        ServerStartEvent::class.java,
        UnregisterPluginChannelsEvent::class.java,
        PlayerDisconnectEvent::class.java,
        ServerHandshakeEvent::class.java,
        PacketReceivedEvent::class.java,
        PlayerJoinEvent::class.java,
        ServerBrandEvent::class.java,
        InstrumentationHotReloadEvent::class.java,
        CustomClickActionEvent::class.java,
        NoxesiumClientInformationEvent::class.java,
        NoxesiumQibTriggeredEvent::class.java,
        NoxesiumPacketReceiveEvent::class.java,
        NoxesiumRiptideEvent::class.java,
        NoxesiumClientSettingsEvent::class.java,
    )
    override val superClass: Class<out Any> = Event::class.java
}


