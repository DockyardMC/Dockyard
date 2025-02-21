package io.github.dockyardmc.server

object FeatureFlags {
    var enabledFeatureFlags = mutableListOf(FeatureFlag.VANILLA)
}

enum class FeatureFlag(var identifier: String) {
    VANILLA("minecraft:vanilla"),
    TRADE_REBALANCE("minecraft:trade_rebalance"),
    REDSTONE_EXPERIMENTS("minecraft:redstone_experiments"),
    MINECART_IMPROVEMENTS("minecraft:minecart_improvements"),
}