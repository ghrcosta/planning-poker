package external.firebase

// Extra definitions used by the "App" Firebase component. They cannot be moved to the App.kt file due to the "JsModule"
// annotations used there, which enforces that all classes are "external".

data class FirebaseOptions(
    @JsName("apiKey") val apiKey: String,
    @JsName("appId") val appId: String,
    @JsName("authDomain") val authDomain: String?,
    @JsName("messagingSenderId") val messagingSenderId: String?,
    @JsName("projectId") val projectId: String?,
    @JsName("storageBucket") val storageBucket: String?,
)