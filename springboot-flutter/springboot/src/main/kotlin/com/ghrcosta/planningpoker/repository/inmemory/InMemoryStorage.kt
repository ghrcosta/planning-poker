package com.ghrcosta.planningpoker.repository.inmemory

import com.ghrcosta.planningpoker.dependency.StorageHandler
import com.ghrcosta.planningpoker.entity.Room
import com.ghrcosta.planningpoker.repository.inmemory.entity.RoomInMemoryEntity
import com.ghrcosta.planningpoker.repository.inmemory.entity.toInMemoryEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnProperty(name= ["database"], havingValue="in-memory")
class InMemoryStorage: StorageHandler {
    val rooms = mutableListOf<RoomInMemoryEntity>()

    override suspend fun findRoomById(id: String): Room? = rooms.find { it.id == id }?.toDomainEntity()

    override suspend fun saveRoom(room: Room) {
        val roomEntity = room.toInMemoryEntity()
        rooms.removeIf { it.id == roomEntity.id }
        rooms.add(roomEntity)
    }

    override suspend fun deleteAllRooms() {
        rooms.clear()
    }
}