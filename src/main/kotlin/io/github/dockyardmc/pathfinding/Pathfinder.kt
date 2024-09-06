package io.github.dockyardmc.pathfinding

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.location.Location
import kotlin.properties.Delegates


class Pathfinder(
    val goal: Location,
    val entity: Entity
) {

    // double buffer that shit
    lateinit var computingPath: Path
    lateinit var path: Path

    var minimumDistance by Delegates.notNull<Double>()

}

