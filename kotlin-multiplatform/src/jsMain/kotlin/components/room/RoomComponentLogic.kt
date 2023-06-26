package components.room

import HOST
import LocalData
import Room
import config.Network
import external.firebase.doc
import external.firebase.onSnapshot
import firestore
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.core.use
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

suspend fun sync() = coroutineScope {
    val room = LocalData.room
    if (room == null) {
        console.log("Missing room data")
        return@coroutineScope
    }
    val roomId = room.id

    onSnapshot(
        reference = doc(firestore, "room", roomId),
        onNext = { snapshot ->
            val roomStr = JSON.stringify(snapshot.data())
            val updatedRoom = Json.decodeFromString<Room>(roomStr)
            LocalData.room = updatedRoom
            updateRoomUI()
        },
        onError = { error -> console.log("Firestore error: Code=${error.code}, Message=${error.message}") },
        onCompletion = null
    )
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