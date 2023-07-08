package com.ghrcosta.planningpoker.service

import com.ghrcosta.planningpoker.dependency.StorageHandler
import com.ghrcosta.planningpoker.entity.Participant
import com.ghrcosta.planningpoker.entity.Room
import com.ghrcosta.planningpoker.exception.ParticipantNotFoundException
import com.ghrcosta.planningpoker.exception.RoomNotFoundException
import org.slf4j.LoggerFactory

@Suppress("unused")  // Not used internally, but accessed by clients
class RoomService(
    private val storage: StorageHandler
) {
    private val log = LoggerFactory.getLogger(RoomService::class.java)

    suspend fun createRoom(): Room {
        var newRoom: Room
        do {
            newRoom = Room()
        } while (storage.findRoomById(newRoom.id) != null)

        storage.saveRoom(newRoom)
        log.info("Created room ${newRoom.id}")
        return newRoom
    }

    suspend fun addParticipant(roomId: String, participantName: String): Room {
        val room = storage.findRoomById(roomId) ?: throw RoomNotFoundException(roomId)

        room.addParticipant(Participant(name = participantName))
        storage.saveRoom(room)

        log.info("Room ${roomId}: added participant '${participantName}'")
        return room
    }

    suspend fun setParticipantVote(roomId: String, participantName: String, vote: String) {
        val room = storage.findRoomById(roomId) ?: throw RoomNotFoundException(roomId)

        room.findParticipantByName(participantName)
            ?.apply { this.vote = vote }
            ?: throw ParticipantNotFoundException(roomId = roomId, participantName = participantName)

        storage.saveRoom(room)
        log.info("Room ${roomId}: participant '${participantName}' voted '${vote}'")
    }

    suspend fun revealVotes(roomId: String, participantName: String) {
        val room = storage.findRoomById(roomId) ?: throw RoomNotFoundException(roomId)

        room.findParticipantByName(participantName)
            ?: throw ParticipantNotFoundException(roomId = roomId, participantName = participantName)

        room.revealVotes()

        storage.saveRoom(room)
        log.info("Room ${roomId}: votes revealed")
    }

    suspend fun clearVotes(roomId: String, participantName: String) {
        val room = storage.findRoomById(roomId) ?: throw RoomNotFoundException(roomId)

        room.findParticipantByName(participantName)
            ?: throw ParticipantNotFoundException(roomId = roomId, participantName = participantName)

        room.clearParticipantsVotes()

        storage.saveRoom(room)
        log.info("Room ${roomId}: votes cleared")
    }

    suspend fun deleteAllRooms() {
        storage.deleteAllRooms()
        log.info("Deleted all rooms")
    }
}