package io.github.dockyardmc

object FeatureFlags {
    var enabledFeatureFlags = mutableListOf(FeatureFlag.VANILLA)
}

enum class FeatureFlag(var identifier: String) {
    VANILLA("minecraft:vanilla"),
    BUNDLE("minecraft:bundle"),
    TRADE_REBALANCE("minecraft:trade_rebalance"),
    UPDATE_1_21("minecraft:update_1_21"),
}