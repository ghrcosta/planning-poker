package config

import ParticipantSession
import Room
import application.RoomService
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.ContentTransformationException
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
import kotlinx.coroutines.launch
import kotlinx.html.HTML
import pollingManager
import storage.Storage

fun Application.configureRouting() {
    routing {

        // Endpoint called by GAE. Must return 200-299 or 404, otherwise GAE will restart the application.
        // See: https://cloud.google.com/appengine/docs/standard/java-gen2/how-instances-are-managed#startup
        get("/_ah/start") {
            call.respond(status = HttpStatusCode.NoContent, message = "")
        }

        post("/create") {
            val newRoom = RoomService().createRoom()
            call.respond(newRoom)
        }

        route("/{roomId}") {
            put("/addParticipant") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = call.request.queryParameters["name"] ?: throw IllegalArgumentException("Name required")

                val room = RoomService().addParticipant(roomId = roomId, participantName = name)

                call.sessions.set(ParticipantSession(roomId, name))
                call.respond(room)
            }

            put("/vote") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = getParticipantName(call, roomId)

                val vote = call.request.queryParameters["vote"] ?: throw IllegalArgumentException("Vote required")

                RoomService().setVote(roomId = roomId, participantName = name, vote = vote)

                call.respond(status = HttpStatusCode.NoContent, message = "")
            }

            post("/revealVotes") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = getParticipantName(call, roomId)

                RoomService().revealVotes(roomId = roomId, participantName = name)

                call.respond(status = HttpStatusCode.NoContent, message = "")
            }

            post("/clearVotes") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = getParticipantName(call, roomId)

                RoomService().clearVotes(roomId = roomId, participantName = name)

                call.respond(status = HttpStatusCode.NoContent, message = "")
            }

            post("/sync") {
                // If "roomId" is null then the route will not be found (since it will not match a valid route), in
                // which case the response will be NotFound (404).
                val roomId = call.parameters["roomId"]!!

                val name = getParticipantName(call, roomId)

                val clientRoomState: Room = try {
                    call.receive()
                } catch (e: ContentTransformationException) {
                    Room("unknown")
                }

                RoomService().sync(
                    roomId = roomId,
                    participantName = name,
                    clientRoomState = clientRoomState,
                    doOnUpdateReceived = { room -> launch { call.respond(room) } },
                    doOnTimeout = { launch { call.respond(status = HttpStatusCode.NotModified, message = "") } }
                )
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

            get {
                call.respondHtml(HttpStatusCode.OK, HTML::defaultPage)
            }
        }

        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::defaultPage)
        }

        static("/static") {
            // Includes the javascript file
            resources()
        }
    }
}

private fun getParticipantName(call: ApplicationCall, roomId: String): String {
    val session = call.sessions.get<ParticipantSession>()
    val sessionRoomId = session?.roomId ?: ""
    if (roomId != sessionRoomId) {
        throw IllegalArgumentException("Session mismatch")
    }
    return session?.participantName ?: throw IllegalArgumentException("Participant unknown")
}