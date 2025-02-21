package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.server.FeatureFlags

@EventDocumentation("server sends feature flags to player", false)
class PlayerSendFeatureFlagsEvent(var featureFlags: MutableList<FeatureFlags.Flag>, override val context: Event.Context) : Event {
}