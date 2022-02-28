package application

import Room
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Since we cannot use websockets in GAE standard environment we have to rely on long polling. With this architecture,
 * when a client submit a request the server will hold it until new data becomes available or a maximum timeout limit is
 * reached. Then the client submit another request, and this loop continues forever.
 *
 * This class implements the mechanism where clients subscribe to new updates and the server submits the new data as it
 * becomes available.
 */
class PollingManager {

    companion object {
        // In GAE standard env Gen2, requests time out after 10 minutes (automatic scaling) or 24 hours (basic scaling).
        // See details at: https://cloud.google.com/appengine/docs/standard/java-gen2/how-instances-are-managed.
        // We are using a lower value here just in case the client causes any issues.
        val POLLING_TIMEOUT_MILLISECONDS = 2.toDuration(DurationUnit.MINUTES).inWholeMilliseconds
    }

    // In Kotlin, a Sequence is a stream of data where the items are returned one after another (see "yield()").
    // A Flow is like an asynchronous Sequence, where there might be a random delay before yielding the next item.
    // Finally, a SharedFlow is a Flow that never ends, so clients keep waiting until the next item is available.
    private val _updates = MutableSharedFlow<Room>()
    val updates = _updates.asSharedFlow()

    suspend fun sendUpdate(updatedRoom: Room) {
        _updates.emit(updatedRoom) // suspends until all subscribers receive it
    }
}