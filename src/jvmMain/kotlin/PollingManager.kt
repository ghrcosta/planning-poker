import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PollingManager {

    companion object {
        val POLLING_TIMEOUT_MILLISECONDS = 2.toDuration(DurationUnit.MINUTES).inWholeMilliseconds
    }

    private val _updates = MutableSharedFlow<Room>()
    val updates = _updates.asSharedFlow()

    suspend fun sendUpdate(updatedRoom: Room) {
        _updates.emit(updatedRoom) // suspends until all subscribers receive it
    }
}