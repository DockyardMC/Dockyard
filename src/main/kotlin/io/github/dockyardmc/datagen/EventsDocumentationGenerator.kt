package io.github.dockyardmc.datagen

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.EventDocumentation
import java.io.File
import java.time.Instant
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

class EventsDocumentationGenerator {

    init {
        log("Generating event documentation..")
        val start = Instant.now().toEpochMilli()
        val packageToScan = "io.github.dockyardmc"

        val reflections = org.reflections.Reflections(packageToScan)
        val annotatedClasses = reflections.getTypesAnnotatedWith(EventDocumentation::class.java)

        val markdown = buildString {
            append("# Event List\n")
            annotatedClasses.forEach { loopClass ->
                val annotation = loopClass.getAnnotation(EventDocumentation::class.java)
                val fields = loopClass.kotlin.declaredMemberProperties
                appendLine("## `${loopClass.simpleName}`")
                appendLine("This event is dispatched ${annotation.description}")
                appendLine("")
                appendLine("Event is cancellable: `${annotation.cancellable}`")
                appendLine("")
                appendLine("Fields:")
                fields.forEach { field ->
                    val fullTypeName = field.javaField!!.genericType.toString()
                        .replace("java.lang.", "")
                        .replace(Regex("(\\w+\\.)+(\\w+)"), "$2") // Keep only the last part of any package name
                        .replace("class ", "")
                        .replace("interface ", "")

                    val nullableIndicator = if (field.returnType.isMarkedNullable) "?" else ""
                    appendLine("- ${field.name}: `${fullTypeName}$nullableIndicator`")
                }
                appendLine("")
                log("Generated event documentation for ${loopClass.simpleName}", LogType.DEBUG)
            }
        }

        val dir = File("./out/")
        dir.mkdirs()
        val file = File("./out/event-list.md")
        file.createNewFile()
        file.writeText(markdown)

        val end = Instant.now().toEpochMilli()
        val now = end - start
        log("Finished generating event documentation in ${now}ms!", LogType.SUCCESS)
    }
}