package components.room

import HOST
import LocalData
import Room
import config.Network
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.utils.io.core.use
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

suspend fun sync() = coroutineScope {
    var room = LocalData.room
    if (room == null) {
        console.log("Missing room data")
        return@coroutineScope
    }
    val roomId = room.id
    val url = "${HOST}/${roomId}/sync"

    while(true) {
        try {
            val response: HttpResponse = Network.getClient().use {
                it.post(url) {
                    contentType(ContentType.Application.Json)
                    body = room!!
                }
            }
            val responseText = response.readText()

            if (response.status == HttpStatusCode.OK) {
                val updatedRoom: Room? = Json.decodeFromString(responseText)
                if (updatedRoom != null) {
                    room = updatedRoom
                    LocalData.room = room
                    updateRoomUI()
                }
            }
        } catch (e: Throwable) {
            // Nothing to do, will retry sync
        }
    }
}

suspend fun sendVote(vote: String) = coroutineScope {
    val roomId = LocalData.room?.id
        ?: run {
            console.log("Missing room data")
            return@coroutineScope
        }
    val url = "${HOST}/${roomId}/vote?vote=${vote}"

    val response: HttpResponse = Network.getClient().use { it.put(url) }
    val responseText = response.readText()

    if (response.status != HttpStatusCode.NoContent) {
        Network.showRequestErrorAlert(responseText)
        return@coroutineScope
    }
}

suspend fun revealVotes() = coroutineScope {
    val roomId = LocalData.room?.id
        ?: run {
            console.log("Missing room data")
            return@coroutineScope
        }
    val url = "${HOST}/${roomId}/revealVotes"

    val response: HttpResponse = Network.getClient().use { it.post(url) }
    val responseText = response.readText()

    if (response.status != HttpStatusCode.NoContent) {
        Network.showRequestErrorAlert(responseText)
        return@coroutineScope
    }
}

suspend fun clearVotes() = coroutineScope {
    val roomId = LocalData.room?.id
        ?: run {
            console.log("Missing room data")
            return@coroutineScope
        }
    val url = "${HOST}/${roomId}/clearVotes"

    val response: HttpResponse = Network.getClient().use { it.post(url) }
    val responseText = response.readText()

    if (response.status != HttpStatusCode.NoContent) {
        Network.showRequestErrorAlert(responseText)
        return@coroutineScope
    }
}