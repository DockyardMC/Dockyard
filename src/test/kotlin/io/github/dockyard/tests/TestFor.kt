package io.github.dockyard.tests

import kotlin.reflect.KClass

/**
 * Specifies which class this test is testing,
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TestFor(vararg val value: KClass<*>)