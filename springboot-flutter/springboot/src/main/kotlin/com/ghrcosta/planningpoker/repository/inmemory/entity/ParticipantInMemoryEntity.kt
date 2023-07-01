package com.ghrcosta.planningpoker.repository.inmemory.entity

import com.ghrcosta.planningpoker.entity.Participant

data class ParticipantInMemoryEntity(
    val name: String,
    var vote: String? = null,
) {
    fun toDomainEntity(): Participant {
        return Participant(
            name = this.name,
            vote = this.vote
        )
    }
}

fun Participant.toInMemoryEntity(): ParticipantInMemoryEntity {
    return ParticipantInMemoryEntity(
        name = this.name,
        vote = this.vote
    )
}