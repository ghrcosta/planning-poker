package com.ghrcosta.planningpoker.util

/**
 * When retrieving data from the database, it is converted into application objects via reflection, and to do that it
 * requires that the object classes have an empty constructor. We are using Kotlin data classes though, which don't have
 * empty constructors, so conversion fails.
 *
 * The workaround is to use Kotlin's NoArg gradle plugin, which creates empty constructors at compile time for classes
 * annotated with the specified annotations (configured in build.gradle).
 *
 * For more details, see [https://kotlinlang.org/docs/no-arg-plugin.html]
 */
@Target(AnnotationTarget.CLASS)
annotation class NoArgConstructor