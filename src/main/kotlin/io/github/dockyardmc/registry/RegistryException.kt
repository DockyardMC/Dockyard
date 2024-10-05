package io.github.dockyardmc.registry

class RegistryException(val identifier: String, val mapSize: Int): Exception() {

    constructor(id: Int, mapSize: Int): this(id.toString(), mapSize)

    override val message: String?
        get() {
            return if(mapSize == 0) {
                "Registry belonging to identifier/protocol id $identifier is not initialized yet! (Make sure to create the DockyardServer object before using registries, listening on events or registering commands"
            } else {
                "Registry entry with identifier $identifier was not found"
            }
        }
}