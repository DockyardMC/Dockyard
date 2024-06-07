package io.github.dockyardmc.entity

import java.util.concurrent.atomic.AtomicInteger

object EntityManager {

    var entityIdCounter = AtomicInteger()
    val entities: MutableList<Entity> = mutableListOf()

}