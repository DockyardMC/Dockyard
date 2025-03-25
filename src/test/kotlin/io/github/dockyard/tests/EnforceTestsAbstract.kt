package io.github.dockyard.tests

import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Modifier
import kotlin.jvm.kotlin
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Forces all classes in a package to have a unit test.
 *
 * Use [TestFor] if your test name doesn't just suffix
 * prod class with `Test`
 */
abstract class EnforceTestsAbstract {
    /**
     * Package as a string in test source directory
     *
     * Example: `io.github.dockyard.tests.package`
     */
    abstract val testsPackage: String

    /**
     * Package as a string in main source directory
     */
    abstract val prodPackage: String

    /**
     * Classes ignored from checking
     */
    abstract val ignoredClasses: List<Class<*>>

    /**
     * Only test classes that inherit this class
     *
     * Examples:
     * [Any], [io.github.dockyardmc.events.Event]
     */
    open val superClass: Class<out Any> = Any::class.java

    open fun shouldTest(cls: Class<*>): Boolean {
        return !Modifier.isAbstract(cls.modifiers)
                && !Modifier.isInterface(cls.modifiers)
                && !cls.kotlin.isCompanion
                && !ignoredClasses.contains(cls)
    }

    @Test
    fun check() {
        val refl = Reflections(
            ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(prodPackage))
                .setScanners(SubTypesScanner(false))
                .filterInputsBy { it.startsWith("$prodPackage.") }
                .useParallelExecutor()
        )

        val testRefl = Reflections(
            ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(testsPackage))
                .setScanners(SubTypesScanner(false))
                .filterInputsBy { it.startsWith("$testsPackage.") }
                .useParallelExecutor()
        )

        val classes = refl.getSubTypesOf(superClass)
        classes.removeAll { !shouldTest(it) }

        val testClasses = testRefl.getSubTypesOf(Any::class.java)

        assert(classes.isNotEmpty()) { "Couldn't load Event classes" }
        assert(testClasses.isNotEmpty()) { "Couldn't load test classes" }

        testClasses.forEach { testClass ->
            val testFor = testClass.getAnnotation(TestFor::class.java)

            if (testFor != null) {
                classes.removeAll(testFor.value.map { it.java }.toSet())
            } else {
                val className = testClass.name
                    .replace(testsPackage, prodPackage)
                    .removeSuffix("Test")

                runCatching {
                    classes.remove(Class.forName(className))
                }
            }
        }

        if (classes.isNotEmpty()) {
            classes.forEach(System.err::println)
        }
        assertTrue(classes.isEmpty(), "Missing tests!1!11!!")
    }
}
