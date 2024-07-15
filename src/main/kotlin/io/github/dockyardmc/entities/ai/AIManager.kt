package io.github.dockyardmc.entities.ai

class AIManager {
    val memory: MutableMap<String, AIMemory<Any>> = mutableMapOf()
}

interface AIMemory<T> {
    val value: T
}

data class ShortTermMemory<T>(val forgetAfter: Int, override val value: T): AIMemory<T>
data class LongTermMemory<T>(override val value: T): AIMemory<T>