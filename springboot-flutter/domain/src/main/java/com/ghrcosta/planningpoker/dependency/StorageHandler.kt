package com.ghrcosta.planningpoker.dependency

import com.ghrcosta.planningpoker.entity.Room

interface StorageHandler {
    suspend fun findRoomById(id: String): Room?
    suspend fun saveRoom(room: Room)
    suspend fun deleteAllRooms()
}