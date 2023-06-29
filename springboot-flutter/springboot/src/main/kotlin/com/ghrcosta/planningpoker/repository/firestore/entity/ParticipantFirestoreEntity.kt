package com.ghrcosta.planningpoker.repository.firestore.entity

import com.ghrcosta.planningpoker.util.NoArgConstructor
import com.ghrcosta.planningpoker.entity.Participant

@NoArgConstructor
data class ParticipantFirestoreEntity(
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

fun Participant.toFirestoreEntity(): ParticipantFirestoreEntity {
    return ParticipantFirestoreEntity(
        name = this.name,
        vote = this.vote
    )
}