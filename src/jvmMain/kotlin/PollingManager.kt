import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PollingManager {

    companion object {
        const val POLLING_TIMEOUT_MILLISECONDS = 2000L
    }

    private val _updates = MutableSharedFlow<Room>()
    val updates = _updates.asSharedFlow()

    suspend fun sendUpdate(updatedRoom: Room) {
        _updates.emit(updatedRoom) // suspends until all subscribers receive it
    }
}