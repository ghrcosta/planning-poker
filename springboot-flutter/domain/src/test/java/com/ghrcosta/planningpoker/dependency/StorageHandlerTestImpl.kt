package com.ghrcosta.planningpoker.dependency

import com.ghrcosta.planningpoker.entity.Room

class StorageHandlerTestImpl: StorageHandler {
    val rooms = mutableListOf<Room>()

    override suspend fun findRoomById(id: String): Room? = rooms.find { it.id == id }

    override suspend fun saveRoom(room: Room) {
        rooms.removeIf { it.id == room.id }
        rooms.add(room)
    }

    override suspend fun deleteAllRooms() {
        rooms.clear()
    }
}