import PollingManager.Companion.POLLING_TIMEOUT_MILLISECONDS
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withTimeout
import kotlinx.html.HTML
import storage.Storage
import java.util.UUID

fun Application.configureRouting() {
    routing {

        // Endpoint called by GAE. Must return 200-299 or 404, otherwise GAE will restart the application.
        // See: https://cloud.google.com/appengine/docs/standard/java-gen2/how-instances-are-managed#startup
        get("/_ah/start") {
            call.respond(status = HttpStatusCode.NoContent, message = "")
        }

        post("/create") {
            val storage = Storage.newInstance()

            var newRoom: Room
            do {
                newRoom = Room(UUID.randomUUID().toString())
            } while (storage.getRoom(newRoom.id) != null)

            storage.setRoom(newRoom)

            call.respond(newRoom)
        }

        route("/{roomId}") {
            put("/addParticipant") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = call.request.queryParameters["name"] ?: throw IllegalArgumentException("Name required")

                val storage = Storage.newInstance()
                val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")

                if (room.participants.any { it.name == name }) {
                    throw IllegalArgumentException("Room already contains a participant with that name")
                }

                room.participants.plus(Participant(name = name))

                storage.setRoom(room)

                call.sessions.set(ParticipantSession(roomId, name))
                call.respond(room)
            }

            put("/vote") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = getParticipantName(call, roomId)

                val vote = call.request.queryParameters["vote"] ?: throw IllegalArgumentException("Vote required")

                val storage = Storage.newInstance()
                val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")

                room.participants.find { it.name == name }
                        ?.apply { this.vote = vote }
                        ?: throw NoSuchElementException("Participant not found")

                storage.setRoom(room)

                call.respond(status = HttpStatusCode.NoContent, message = "")
            }

            post("/revealVotes") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = getParticipantName(call, roomId)

                val storage = Storage.newInstance()
                val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")

                assertParticipantIsInRoom(room, name)

                room.votesRevealed = true

                storage.setRoom(room)

                call.respond(status = HttpStatusCode.NoContent, message = "")
            }

            post("/clearVotes") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = getParticipantName(call, roomId)

                val storage = Storage.newInstance()
                val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")
                assertParticipantIsInRoom(room, name)

                room.votesRevealed = false
                room.participants.forEach {
                    it.vote = null
                }

                storage.setRoom(room)

                call.respond(status = HttpStatusCode.NoContent, message = "")
            }

            post("/sync") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = getParticipantName(call, roomId)

                val storage = Storage.newInstance()
                val room = storage.getRoom(roomId) ?: throw NoSuchElementException("Room not found")
                assertParticipantIsInRoom(room, name)

                val clientRoomState = call.receive<Room>()
                if (clientRoomState == room) {
                    val deferred = async {
                        withTimeout(POLLING_TIMEOUT_MILLISECONDS) {
                            pollingManager.updates.collect { room ->
                                call.respond(room)
                            }
                        }
                    }
                    deferred.await()
                }
            }

            post("/notifyUpdated") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val storage = Storage.newInstance()
                storage.getRoom(roomId)?.let { room ->
                    pollingManager.sendUpdate(room)
                }

                call.respond(status = HttpStatusCode.NoContent, message = "")
            }
        }






        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }

        static("/static") {
            resources()
        }
    }
}

fun getParticipantName(call: ApplicationCall, roomId: String): String {
    val session = call.sessions.get<ParticipantSession>()
    val sessionRoomId = session?.roomId ?: ""
    if (roomId != sessionRoomId) {
        throw IllegalArgumentException("Session mismatch")
    }
    return session?.participantName ?: throw IllegalArgumentException("Participant unknown")
}


fun assertParticipantIsInRoom(room: Room, participantName: String) {
    if (room.participants.none { it.name == participantName }) {
        throw IllegalArgumentException("Participant not found")
    }
}