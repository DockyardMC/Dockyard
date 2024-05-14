package io.github.dockyardmc.events

import io.github.dockyardmc.FeatureFlag
import io.github.dockyardmc.annotations.EventDocumentation

@EventDocumentation("server sends feature flags to client during configuration", false)
class ServerFeatureFlagsEvent(var featureFlags: MutableList<FeatureFlag>): Event