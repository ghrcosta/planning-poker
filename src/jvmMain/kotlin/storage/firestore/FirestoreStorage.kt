package storage.firestore

import Room
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import storage.Storage

/**
 * Implementation to store data on Google Cloud Firestore, a NoSQL database.
 */
class FirestoreStorage(
    private val db: Firestore = initialize()
): Storage() {

    companion object {
        private const val COLLECTION_ROOMS = "room"

        private fun initialize(): Firestore {
            return try {
                FirestoreClient.getFirestore()
            } catch (e: Throwable) {
                // See: https://firebase.google.com/docs/firestore/quickstart#initialize
                // Database is in the same GCP project as backend, so we can authenticate using default credentials.
                // It works for the emulator as well; check instructions for application-default credentials in README.
                val options =
                    FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build()
                FirebaseApp.initializeApp(options)
                FirestoreClient.getFirestore()
            }
        }
    }

    override fun getRoom(roomId: String): Room? {
        val docRef = db.collection(COLLECTION_ROOMS).document(roomId)
        val result = docRef.get()
        val doc = result.get()

        return doc.data?.let {
            doc.toObject(Room::class.java)
        }
    }

    override fun setRoom(room: Room) {
        val docRef = db.collection(COLLECTION_ROOMS).document(room.id)
        val result = docRef.set(room)
        result.get()  // Execute synchronously
    }
}