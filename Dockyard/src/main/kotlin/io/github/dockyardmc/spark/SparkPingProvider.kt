package io.github.dockyardmc.spark

import io.github.dockyardmc.player.PlayerManager
import me.lucko.spark.common.monitor.ping.PlayerPingProvider

class SparkPingProvider: PlayerPingProvider {

    override fun poll(): MutableMap<String, Int> {
        val map = mutableMapOf<String, Int>()

        PlayerManager.players.forEach { player ->
            map[player.username] = 0 //TODO Ping
        }

        return map
    }
}