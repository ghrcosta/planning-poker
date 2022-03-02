package storage

import Room
import storage.firestore.FirestoreStorage

/**
 * Generic interface to access storage solution. To use it, call [Storage.getInstance()][newInstance].
 */
abstract class Storage {

    companion object {
        /** Acts as a factory, to retrieve an instance of the implementation we want to use. */
        fun newInstance(): Storage = FirestoreStorage()
    }

    abstract fun getRoom(roomId: String): Room?

    abstract fun setRoom(room: Room)

    abstract fun deleteAllRooms()
}