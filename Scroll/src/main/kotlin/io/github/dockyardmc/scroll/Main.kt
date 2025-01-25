package io.github.dockyardmc.scroll

import io.github.dockyardmc.scroll.serializers.JsonToComponentSerializer

fun main() {
    val json = JsonToComponentSerializer.serialize("{\"color\":\"gray\",\"extra\":[{\"color\":\"white\",\"text\":\">\"},\" \",\"Max: \",\"53.19\",\"ms\"],\"text\":\"\"}")
    println(json.toJson())
}