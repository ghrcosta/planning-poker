package com.ghrcosta.planningpoker.repository.inmemory.entity

import com.ghrcosta.planningpoker.entity.Room

data class RoomInMemoryEntity(
    val id: String,
    val participants: List<ParticipantInMemoryEntity>,
    val votesRevealed: Boolean,
) {
    fun toDomainEntity(): Room {
        return Room(
            id = this.id,
            participants = this.participants.map { it.toDomainEntity() },
            votesRevealed = this.votesRevealed
        )
    }
}

fun Room.toInMemoryEntity(): RoomInMemoryEntity {
    return RoomInMemoryEntity(
        id = this.id,
        participants = this.participants.map { it.toInMemoryEntity() },
        votesRevealed = this.votesRevealed,
    )
}