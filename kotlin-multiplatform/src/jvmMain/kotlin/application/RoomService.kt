package application

import Participant
import Room
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.Storage
import kotlin.random.Random

class RoomService {
    private val logger: Logger = LoggerFactory.getLogger(RoomService::class.java)

    private val storage = Storage.newInstance()
    private val alphanumericChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun createRoom(): Room {
        var newRoom: Room
        do {
            newRoom = Room(createRandomString())
        } while (storage.getRoom(newRoom.id) != null)

        storage.setRoom(newRoom)
        logger.info("Created room ${newRoom.id}")
        return newRoom
    }

    private fun createRandomString(): String {
        return (1..4)
            .map { Random.nextInt(0, alphanumericChars.size) }
            .map(alphanumericChars::get)
            .joinToString("")
    }

    fun addParticipant(roomId: String, participantName: String): Room {
        val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")

        if (room.participants.any { it.name == participantName }) {
            throw IllegalArgumentException("Room already contains a participant with that name")
        }

        room.participants = room.participants.plus(Participant(name = participantName))

        storage.setRoom(room)
        logger.info("Room ${roomId}: added participant '${participantName}'")
        return room
    }

    fun setVote(roomId: String, participantName: String, vote: String) {
        val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")

        room.participants.find { it.name == participantName }
            ?.apply { this.vote = vote }
            ?: throw NoSuchElementException("Participant not found")

        storage.setRoom(room)
        logger.info("Room ${roomId}: participant '${participantName}' voted '${vote}'")
    }

    fun revealVotes(roomId: String, participantName: String) {
        val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")

        assertParticipantIsInRoom(room, participantName)

        room.votesRevealed = true

        storage.setRoom(room)
        logger.info("Room ${roomId}: votes revealed")
    }

    fun clearVotes(roomId: String, participantName: String) {
        val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")
        assertParticipantIsInRoom(room, participantName)

        room.votesRevealed = false
        room.participants.forEach {
            it.vote = null
        }

        storage.setRoom(room)
        logger.info("Room ${roomId}: votes cleared")
    }

    fun deleteAllRooms() {
        storage.deleteAllRooms()
        logger.info("Deleted all rooms")
    }

    private fun assertParticipantIsInRoom(room: Room, participantName: String) {
        if (room.participants.none { it.name == participantName }) {
            throw IllegalArgumentException("Participant not found")
        }
    }
}