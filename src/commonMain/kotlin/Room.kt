import kotlinx.serialization.Serializable

@Serializable
data class Room(
        val id: String,
        var participants: List<Participant> = emptyList(),
        var voteOptions: List<String> = listOf("1/2", "1", "2", "3", "5", "8", "13", "?"),
        var votesRevealed: Boolean = false,
)