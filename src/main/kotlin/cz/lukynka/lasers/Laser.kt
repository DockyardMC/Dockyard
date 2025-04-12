package cz.lukynka.lasers

import io.github.dockyardmc.entity.BlockDisplay
import io.github.dockyardmc.location.Location

class Laser(private val initialLocation: Location) {

    val entity: BlockDisplay? = null
    val location get() = entity?.location ?: initialLocation

    fun spawn() {
        if(entity != null) return

    }

}