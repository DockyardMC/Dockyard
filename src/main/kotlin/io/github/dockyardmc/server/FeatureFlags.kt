package io.github.dockyardmc.server

object FeatureFlags {
    var enabledFlags = mutableListOf(Flag.VANILLA)

    enum class Flag(var identifier: String) {
        VANILLA("minecraft:vanilla"),
        TRADE_REBALANCE("minecraft:trade_rebalance"),
        REDSTONE_EXPERIMENTS("minecraft:redstone_experiments"),
        MINECART_IMPROVEMENTS("minecraft:minecart_improvements"),
    }
}

