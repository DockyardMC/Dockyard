package io.github.dockyardmc.protocol.proxy

object LegacyBungeeCordProxySupport {
    val bungeeGuardTokens: MutableSet<String>? = null
    var enabled: Boolean = false

    fun isBungeeGuardEnabled(): Boolean = bungeeGuardTokens != null

    fun isValidBungeecordGuardToken(token: String): Boolean {
        return enabled && bungeeGuardTokens?.contains(token) ?: false
    }

}