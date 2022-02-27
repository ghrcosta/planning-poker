import kotlinx.serialization.Serializable

@Serializable
data class Participant(
        val name: String,
        var vote: String? = null,
)