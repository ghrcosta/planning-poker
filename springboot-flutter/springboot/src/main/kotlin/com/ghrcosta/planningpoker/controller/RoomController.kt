package com.ghrcosta.planningpoker.controller

import com.ghrcosta.planningpoker.dto.RoomDTO
import com.ghrcosta.planningpoker.dto.RoomDTO.Companion.dto
import com.ghrcosta.planningpoker.exception.DuplicatedParticipantNameException
import com.ghrcosta.planningpoker.exception.ParticipantNotFoundException
import com.ghrcosta.planningpoker.exception.RoomNotFoundException
import com.ghrcosta.planningpoker.service.RoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "roomApi", description = "API to be used by clients")
class RoomController(private val roomService: RoomService) {

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new room",
        description = "Create a new room. Every new room is guaranteed to have a different ID.")
    @ApiResponse(
        responseCode = "201",
        description = "New room created and stored in database",
        content = [Content(schema = Schema(implementation = RoomDTO::class))])
    fun createRoom(): RoomDTO = runBlocking {
        val newRoom = roomService.createRoom()
        return@runBlocking newRoom.dto()
    }

    @PutMapping("/{roomId}/addParticipant")
    @Operation(
        summary = "Add new participant to a room",
        description = "Adds the given name as a participant in the given room and sets cookies used by other requests.")
    @ApiResponse(
        responseCode = "204",
        description = "Database updated",
        headers = [Header(name = "Set-Cookie", description = "Sets cookies for 'roomId' and 'participant'")])
    fun addParticipant(
        @PathVariable("roomId") roomId: String,
        @RequestParam("name") participantName: String,
    ): ResponseEntity<Void> = runBlocking {
        roomService.addParticipant(roomId = roomId, participantName = participantName)

        return@runBlocking ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, ResponseCookie.from(COOKIE_ROOM, roomId).build().toString())
            .header(HttpHeaders.SET_COOKIE, ResponseCookie.from(COOKIE_PARTICIPANT, participantName).build().toString())
            .build()
    }

    @PutMapping("/{roomId}/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Set participant vote",
        description = "Sets vote of a participant in the room. Both room and participant are retrieved from cookies.")
    @ApiResponse(
        responseCode = "204",
        description = "Database updated")
    fun setParticipantVote(
        @PathVariable("roomId") roomId: String,
        @RequestParam("value") vote: String,
        @CookieValue(name = COOKIE_ROOM) cookieRoomId: String,
        @CookieValue(name = COOKIE_PARTICIPANT) cookieParticipant: String,
    ) = runBlocking {
        if (roomId != cookieRoomId) {
            throw IllegalArgumentException("Session mismatch")
        }
        roomService.setParticipantVote(roomId = roomId, participantName = cookieParticipant, vote = vote)
    }

    @PostMapping("/{roomId}/revealVotes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Reveal votes in the room",
        description = "Reveals votes from everyone in the room. Only someone in the room can execute it. " +
                      "Both room and participant are retrieved from cookies.")
    @ApiResponse(
        responseCode = "204",
        description = "Database updated")
    fun revealVotes(
        @PathVariable("roomId") roomId: String,
        @CookieValue(name = COOKIE_ROOM) cookieRoomId: String,
        @CookieValue(name = COOKIE_PARTICIPANT) cookieParticipant: String,
    ) = runBlocking {
        if (roomId != cookieRoomId) {
            throw IllegalArgumentException("Session mismatch")
        }
        roomService.revealVotes(roomId = roomId, participantName = cookieParticipant)
    }

    @PostMapping("/{roomId}/clearVotes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete votes in the room",
        description = "Remove votes from everyone in the room and sets vote visibility to hidden. " +
                      "Only someone in the room can execute it. " +
                      "Both room and participant are retrieved from cookies.")
    @ApiResponse(
        responseCode = "204",
        description = "Database updated")
    fun clearVotes(
        @PathVariable("roomId") roomId: String,
        @CookieValue(name = COOKIE_ROOM) cookieRoomId: String,
        @CookieValue(name = COOKIE_PARTICIPANT) cookieParticipant: String,
    ) = runBlocking {
        if (roomId != cookieRoomId) {
            throw IllegalArgumentException("Session mismatch")
        }
        roomService.clearVotes(roomId = roomId, participantName = cookieParticipant)
    }

    @ExceptionHandler(RoomNotFoundException::class)
    fun handleRoomNotFoundException(e: RoomNotFoundException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DuplicatedParticipantNameException::class)
    fun handleDuplicatedParticipantNameException(e: DuplicatedParticipantNameException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ParticipantNotFoundException::class)
    fun handleParticipantNotFoundException(e: ParticipantNotFoundException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleParticipantNotFoundException(e: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
    }

    companion object {
        private const val COOKIE_ROOM = "roomId"
        private const val COOKIE_PARTICIPANT = "participant"
    }
}