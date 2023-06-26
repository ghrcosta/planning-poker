package config

import external.firebase.FirebaseOptions
import external.firebase.Firestore
import external.firebase.getFirestore
import external.firebase.initializeApp
import kotlinext.js.asJsObject


object FirestoreHelper {
    fun initFirestore(): Firestore {
        val firebaseApp = initializeApp(
            FirebaseOptions(
                apiKey = "YOUR_GCP_DATA_HERE",
                appId = "YOUR_GCP_DATA_HERE",
                authDomain = "YOUR_GCP_DATA_HERE",
                messagingSenderId = "YOUR_GCP_DATA_HERE",
                projectId = "YOUR_GCP_DATA_HERE",
                storageBucket = "YOUR_GCP_DATA_HERE",
            ).asJsObject()
        )
        return getFirestore(firebaseApp)
    }
}