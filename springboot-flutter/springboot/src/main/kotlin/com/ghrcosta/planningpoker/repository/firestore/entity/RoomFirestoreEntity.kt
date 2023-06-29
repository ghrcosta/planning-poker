package com.ghrcosta.planningpoker.repository.firestore.entity

import com.ghrcosta.planningpoker.util.NoArgConstructor
import com.ghrcosta.planningpoker.entity.Room
import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.spring.data.firestore.Document

@NoArgConstructor
@Document(collectionName = "room")
data class RoomFirestoreEntity(
    @DocumentId val id: String,
    val participants: List<ParticipantFirestoreEntity>,
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

fun Room.toFirestoreEntity(): RoomFirestoreEntity {
    return RoomFirestoreEntity(
        id = this.id,
        participants = this.participants.map { it.toFirestoreEntity() },
        votesRevealed = this.votesRevealed,
    )
}