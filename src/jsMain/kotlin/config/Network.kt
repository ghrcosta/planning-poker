package config

import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.browser.window

object Network {
    fun getClient(): HttpClient {
        return HttpClient {
            // https://ktor.io/docs/response-validation.html#default
            expectSuccess = false

            // https://ktor.io/docs/http-cookies.html
            install(HttpCookies)

            // https://ktor.io/docs/json.html#install_feature
            install(JsonFeature){
                // https://ktor.io/docs/json.html#configure_serializer
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    prettyPrint = true
                })
            }
        }
    }

    fun showRequestErrorAlert(responseText: String) {
        if (responseText.isNotBlank()) {
            window.alert(responseText.substringAfter("Exception: "))
        }
    }
}