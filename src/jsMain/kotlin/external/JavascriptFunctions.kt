package external

// Define Javascript functions that KotlinJS does not recognize by default

external fun encodeURIComponent(uri: String): String

external fun decodeURIComponent(encodedURI: String): String