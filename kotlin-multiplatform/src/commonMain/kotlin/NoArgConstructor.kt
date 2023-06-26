/**
 * Firestore "result.get().toObjects()" converts database data into application objects via reflection, and to do that
 * it requires the object classes have an empty constructor. We are using Kotlin data classes though, which don't have
 * empty constructors, so conversion fails.
 *
 * The workaround is to use Kotlin's NoArg gradle plugin, which creates empty constructors at compile time for classes
 * annotated with the specified annotations (configured in build.gradle).
 *
 * For more details, see [https://kotlinlang.org/docs/no-arg-plugin.html]
 */
@Target(AnnotationTarget.CLASS)
annotation class NoArgConstructor()