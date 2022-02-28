package application

import Participant
import Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import pollingManager
import storage.Storage
import java.util.UUID

class RoomService {
    private val storage = Storage.newInstance()

    fun createRoom(): Room {
        var newRoom: Room
        do {
            newRoom = Room(UUID.randomUUID().toString())
        } while (storage.getRoom(newRoom.id) != null)

        storage.setRoom(newRoom)
        return newRoom
    }

    fun addParticipant(roomId: String, participantName: String): Room {
        val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")

        if (room.participants.any { it.name == participantName }) {
            throw IllegalArgumentException("Room already contains a participant with that name")
        }

        room.participants = room.participants.plus(Participant(name = participantName))

        storage.setRoom(room)
        return room
    }

    fun setVote(roomId: String, participantName: String, vote: String) {
        val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")

        room.participants.find { it.name == participantName }
            ?.apply { this.vote = vote }
            ?: throw NoSuchElementException("Participant not found")

        storage.setRoom(room)
    }

    fun revealVotes(roomId: String, participantName: String) {
        val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")

        assertParticipantIsInRoom(room, participantName)

        room.votesRevealed = true

        storage.setRoom(room)
    }

    fun clearVotes(roomId: String, participantName: String) {
        val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")
        assertParticipantIsInRoom(room, participantName)

        room.votesRevealed = false
        room.participants.forEach {
            it.vote = null
        }

        storage.setRoom(room)
    }

    suspend fun sync(
        roomId: String,
        participantName: String,
        clientRoomState: Room,
        doOnUpdateReceived: (Room) -> Unit,
        doOnTimeout: () -> Unit
    ) {
        val storage = Storage.newInstance()
        val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")
        assertParticipantIsInRoom(room, participantName)

        if (clientRoomState != room) {
            // If the client doesn't have the latest data, send it now without waiting for updates. The client
            // should immediately send another sync request to wait for future updates.
            doOnUpdateReceived(room)

        } else {
            // If the client already has the latest data, start long polling to wait until there is an update.
            // If an update is received before the timeout, it is sent to the client. Otherwise, the client is
            // instructed to send another sync request.

            val pollingJob = CoroutineScope(Dispatchers.IO).launch {
                pollingManager.updates.collect { room ->
                    doOnUpdateReceived(room)
                    this.cancel()  // Because SharedFlows never complete, they can only be cancelled
                }
            }
            try {
                withTimeout(PollingManager.POLLING_TIMEOUT_MILLISECONDS) {
                    pollingJob.join()
                }
            } catch (e: TimeoutCancellationException) {
                pollingJob.cancel()
                doOnTimeout()
            }
        }
    }

    private fun assertParticipantIsInRoom(room: Room, participantName: String) {
        if (room.participants.none { it.name == participantName }) {
            throw IllegalArgumentException("Participant not found")
        }
    }
}