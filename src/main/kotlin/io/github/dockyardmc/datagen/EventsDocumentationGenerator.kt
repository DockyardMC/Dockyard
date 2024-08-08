package io.github.dockyardmc.datagen

import io.github.dockyardmc.annotations.EventDocumentation
import java.io.File
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

class EventsDocumentationGenerator {

    init {
        val packageToScan = "io.github.dockyardmc"

        val reflections = org.reflections.Reflections(packageToScan)
        val annotatedClasses = reflections.getTypesAnnotatedWith(EventDocumentation::class.java)

        val markdown = buildString {
            append("# Events\n")
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
            }
        }

        val dir = File("./out/")
        dir.mkdirs()
        val file = File("./out/events.md")
        file.createNewFile()
        file.writeText(markdown)
    }
}