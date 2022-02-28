import kotlinx.serialization.Serializable

@Serializable
@NoArgConstructor
data class Room(
        val id: String,
        var participants: List<Participant> = emptyList(),
        var voteOptions: List<String> = listOf("0", "1/2", "1", "2", "3", "5", "8", "13", "?"),
        var votesRevealed: Boolean = false,
)