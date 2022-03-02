import kotlinx.serialization.Serializable

@Serializable
@NoArgConstructor
data class ParticipantSession(
    val roomId: String,
    val participantName: String,
)