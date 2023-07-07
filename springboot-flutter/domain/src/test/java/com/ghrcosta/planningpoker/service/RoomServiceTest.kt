package com.ghrcosta.planningpoker.service

import com.ghrcosta.planningpoker.dependency.StorageHandlerTestImpl
import com.ghrcosta.planningpoker.exception.ParticipantNotFoundException
import com.ghrcosta.planningpoker.exception.RoomNotFoundException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RoomServiceTest {

    private val storage = StorageHandlerTestImpl()
    private val roomService = RoomService(storage)

    @Test
    fun roomIsCreatedAndStored() = runTest {
        val room = roomService.createRoom()
        assertTrue { storage.findRoomById(room.id) != null }
    }

    @Test
    fun participantIsAdded() = runTest {
        val name = "test"
        val room = roomService.createRoom()

        roomService.addParticipant(room.id, name)
        assertTrue { storage.findRoomById(room.id)?.participants?.any { it.name == name } == true }
    }

    @Test
    fun participantIsNotAddedWhenRoomIdIsInvalid() = runTest {
        assertFailsWith<RoomNotFoundException> {
            roomService.addParticipant("invalidId", "a")
        }
    }

    @Test
    fun participantVoteIsSaved() = runTest {
        val vote = "2"
        val name = "test"
        val room = roomService.createRoom()
        roomService.addParticipant(room.id, name)

        roomService.setParticipantVote(roomId = room.id, participantName = name, vote = vote)
        assertTrue { storage.findRoomById(room.id)?.participants?.find { it.name == name }?.vote == vote }
    }

    @Test
    fun participantVoteIsNotSavedWhenRoomIdIsInvalid() = runTest {
        assertFailsWith<RoomNotFoundException> {
            roomService.setParticipantVote(roomId = "invalidId", participantName = "a", vote = "1")
        }
    }

    @Test
    fun votesAreRevealedWhenRequestedByOneOfTheParticipants() = runTest {
        val nameA = "a"
        val room = roomService.createRoom()
        roomService.addParticipant(room.id, participantName = nameA)

        assertTrue { storage.findRoomById(room.id)?.votesRevealed == false }
        roomService.revealVotes(roomId = room.id, participantName = nameA)
        assertTrue { storage.findRoomById(room.id)?.votesRevealed == true }
    }

    @Test
    fun votesAreNotRevealedWhenRequestedByInvalidParticipant() = runTest {
        val room = roomService.createRoom()
        roomService.addParticipant(room.id, participantName = "a")

        assertFailsWith<ParticipantNotFoundException> {
            roomService.revealVotes(roomId = room.id, participantName = "b")
        }
    }

    @Test
    fun votesAreNotRevealedWhenRoomIdIsInvalid() = runTest {
        assertFailsWith<RoomNotFoundException> {
            roomService.revealVotes(roomId = "invalidId", participantName = "a")
        }
    }

    @Test
    fun canChangeVoteAfterVotesAreRevealed() = runTest {
        val name = "test"
        val room = roomService.createRoom()
        roomService.addParticipant(room.id, name)
        roomService.setParticipantVote(roomId = room.id, participantName = name, vote = "2")
        roomService.revealVotes(roomId = room.id, participantName = name)

        val vote = "3"
        roomService.setParticipantVote(roomId = room.id, participantName = name, vote = vote)
        assertTrue { storage.findRoomById(room.id)?.participants?.find { it.name == name }?.vote == vote }
    }

    @Test
    fun votesAreClearedWhenRequestedByOneOfTheParticipants() = runTest {
        val nameA = "a"
        val room = roomService.createRoom()
        roomService.addParticipant(room.id, participantName = nameA)
        roomService.setParticipantVote(roomId = room.id, participantName = nameA, vote = "2")

        roomService.clearVotes(roomId = room.id, participantName = nameA)
        assertTrue { storage.findRoomById(room.id)?.participants?.all { it.vote == null } == true }
    }

    @Test
    fun votesAreNotClearedWhenRequestedByInvalidParticipant() = runTest {
        val room = roomService.createRoom()
        roomService.addParticipant(room.id, participantName = "a")

        assertFailsWith<ParticipantNotFoundException> {
            roomService.clearVotes(roomId = room.id, participantName = "b")
        }
    }

    @Test
    fun votesAreNotClearedWhenRoomIdIsInvalid() = runTest {
        assertFailsWith<RoomNotFoundException> {
            roomService.clearVotes(roomId = "invalidId", participantName = "a")
        }
    }

    @Test
    fun roomsAreAllDeleted() = runTest {
        roomService.createRoom()

        roomService.deleteAllRooms()
        assertTrue { storage.rooms.isEmpty() }
    }
}