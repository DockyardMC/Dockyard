package io.github.dockyardmc.server

object FeatureFlags {
    var enabledFeatureFlags = mutableListOf(FeatureFlag.VANILLA)
}

enum class FeatureFlag(var identifier: String) {
    VANILLA("minecraft:vanilla"),
    BUNDLE("minecraft:bundle"),
    TRADE_REBALANCE("minecraft:trade_rebalance"),
}