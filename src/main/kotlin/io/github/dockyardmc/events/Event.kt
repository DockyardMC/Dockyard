package io.github.dockyardmc.events

import io.github.dockyardmc.bounds.Bound
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.world.Chunk

interface Event {
    val context: Collection<Any>

    fun Event.elements(vararg elements: Any?): Collection<Any> {
        return buildSet {
            elements.forEach {
                if (it != null) addAll(it.convertContext())
            }
        }
    }

    fun Any.convertContext(): List<Any> {
        return when (this) {
            // return any additional properties which may be associated with this property
            is Entity -> listOf(this.location, this.world)
            is Location -> listOf(this.world)
            is Bound -> listOf(this.world, this.firstLocation, this.secondLocation)
            is Chunk -> listOf(this.world)
            else -> listOf()
        } + this
    }
}