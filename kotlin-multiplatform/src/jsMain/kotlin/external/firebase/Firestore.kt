@file:JsModule("firebase/firestore")
@file:JsNonModule


package external.firebase


// https://firebase.google.com/docs/reference/js/firestore_.firestore.md?authuser=0#firestore_class
external class Firestore

// https://firebase.google.com/docs/reference/js/firestore_.documentreference.md?authuser=0#documentreference_class
external class DocumentReference<@Suppress("unused") T>

// https://firebase.google.com/docs/reference/js/firestore_.unsubscribe.md?authuser=0&hl=en#unsubscribe_interface
external interface Unsubscribe

// https://firebase.google.com/docs/reference/js/firestore_.documentsnapshot.md?authuser=0#documentsnapshot_class
open external class DocumentSnapshot<T> {
    open fun data(options: Any? = definedExternally): T?
}

// https://firebase.google.com/docs/reference/js/firestore_.firestoreerror.md?authuser=0#firestoreerror_class
external interface FirestoreError {
    var code: String // https://firebase.google.com/docs/reference/js/firestore_.md?authuser=0#firestoreerrorcode
    var message: String
}

// https://firebase.google.com/docs/reference/js/firestore_.md?authuser=0&hl=en#getfirestore
external fun getFirestore(app: Any? = definedExternally): Firestore

// https://firebase.google.com/docs/reference/js/firestore_.md?authuser=0&hl=en#connectfirestoreemulator
external fun connectFirestoreEmulator(firestore: Firestore, host: String, port: Int, options: Any? = definedExternally)

// https://firebase.google.com/docs/reference/js/firestore_.md?authuser=0&hl=en#doc
external fun doc(firestore: Firestore, path: String, vararg pathSegments: String): DocumentReference<Any>

// https://firebase.google.com/docs/reference/js/firestore_.md?authuser=0&hl=en#onsnapshot_3
external fun onSnapshot(
    reference: DocumentReference<Any>,
    onNext: (snapshot: DocumentSnapshot<Any>) -> Unit,
    onError: ((error: FirestoreError) -> Unit)? = definedExternally,
    onCompletion: (() -> Unit)? = definedExternally
): Unsubscribe