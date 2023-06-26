import kotlinx.serialization.Serializable

@Serializable
@NoArgConstructor
data class Room(
        val id: String,
        var participants: List<Participant> = emptyList(),
        var votesRevealed: Boolean = false,
)