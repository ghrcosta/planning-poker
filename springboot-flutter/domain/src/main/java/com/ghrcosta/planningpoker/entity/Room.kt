package com.ghrcosta.planningpoker.entity

import com.ghrcosta.planningpoker.exception.DuplicatedParticipantNameException
import kotlin.random.Random

@Suppress("unused")
class Room(
    id: String? = null,
    participants: List<Participant>? = null,
    votesRevealed: Boolean = false
) {
    val id = id ?: createRandomString()

    val participants: List<Participant>
        get() = _participants.toList()

    val votesRevealed: Boolean
        get() = _votesRevealed

    // Backing fields
    private val _participants = participants?.toMutableList() ?: mutableListOf()
    private var _votesRevealed = votesRevealed

    internal fun findParticipantByName(name: String): Participant? = _participants.find { it.name == name }

    internal fun addParticipant(participant: Participant) {
        if (_participants.any { it.name == participant.name }) {
            throw DuplicatedParticipantNameException(roomId = id, participantName = participant.name)
        }
        _participants.add(participant)
    }

    internal fun clearParticipantsVotes() {
        _participants.forEach { it.vote = null }
        _votesRevealed = false
    }

    internal fun revealVotes() {
        _votesRevealed = true
    }

    private fun createRandomString(): String {
        return (1..4)
            .map { Random.nextInt(0, alphanumericChars.size) }
            .map(alphanumericChars::get)
            .joinToString("")
    }

    companion object {
        private val alphanumericChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    }
}