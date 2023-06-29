package com.ghrcosta.planningpoker.entity

import com.ghrcosta.planningpoker.exception.DuplicatedParticipantNameException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class RoomTest {
    @Test
    fun eachRoomHasADifferentId() {
        val roomA = Room()
        val roomB = Room()
        assertNotEquals(roomA.id, roomB.id)
    }

    @Test
    fun cannotHaveTwoParticipantsWithSameName() {
        val room = Room()
        val participantA = Participant(name = "person")
        val participantB = Participant(name = "person")
        room.addParticipant(participantA)
        assertFailsWith<DuplicatedParticipantNameException> { room.addParticipant(participantB) }
    }
}