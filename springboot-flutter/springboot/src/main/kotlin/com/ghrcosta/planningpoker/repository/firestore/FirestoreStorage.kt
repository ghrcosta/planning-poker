package com.ghrcosta.planningpoker.repository.firestore

import com.ghrcosta.planningpoker.dependency.StorageHandler
import com.ghrcosta.planningpoker.entity.Room
import com.ghrcosta.planningpoker.repository.firestore.entity.RoomFirestoreEntity
import com.ghrcosta.planningpoker.repository.firestore.entity.toFirestoreEntity
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnProperty(name= ["database"], havingValue="firestore")
class FirestoreStorage(private val roomFirestoreStorage: RoomFirestoreStorage): StorageHandler {
    override suspend fun findRoomById(id: String): Room? {
        return roomFirestoreStorage.findById(id).block()?.toDomainEntity()
    }

    override suspend fun saveRoom(room: Room) {
        roomFirestoreStorage.save(room.toFirestoreEntity()).block()
    }

    override suspend fun deleteAllRooms() {
        roomFirestoreStorage.deleteAll().block()
    }
}

@Repository
@ConditionalOnProperty(name= ["database"], havingValue="firestore")
interface RoomFirestoreStorage: FirestoreReactiveRepository<RoomFirestoreEntity>