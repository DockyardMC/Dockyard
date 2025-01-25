package io.github.dockyardmc.events

import io.github.dockyardmc.server.FeatureFlag
import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("server sends feature flags to client during configuration", false)
class PlayerSendFeatureFlagsEvent(var featureFlags: MutableList<FeatureFlag>): Event {
    override val context = Event.Context()
}