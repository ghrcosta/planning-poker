import kotlinx.serialization.Serializable

@Serializable
@NoArgConstructor
data class Participant(
        val name: String,
        var vote: String? = null,
)