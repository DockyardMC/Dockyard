package io.github.dockyardmc.events

import io.github.dockyardmc.FeatureFlag

class ServerFeatureFlagsEvent(var featureFlags: MutableList<FeatureFlag>): Event