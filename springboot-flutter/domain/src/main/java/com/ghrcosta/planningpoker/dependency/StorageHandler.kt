package com.ghrcosta.planningpoker.dependency

import com.ghrcosta.planningpoker.entity.Room

interface StorageHandler {
    /** @return A specific room from the database if it exists, null otherwise */
    suspend fun findRoomById(id: String): Room?

    /** Add a room to the database if it doesn't exist, update it otherwise */
    suspend fun saveRoom(room: Room)

    /** Clear the database */
    suspend fun deleteAllRooms()
}