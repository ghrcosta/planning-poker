package com.ghrcosta.planningpoker.controller

import com.ghrcosta.planningpoker.dto.RoomDTO
import com.ghrcosta.planningpoker.dto.RoomDTO.Companion.dto
import com.ghrcosta.planningpoker.dto.SessionDTO
import com.ghrcosta.planningpoker.exception.DuplicatedParticipantNameException
import com.ghrcosta.planningpoker.exception.ParticipantNotFoundException
import com.ghrcosta.planningpoker.exception.RoomNotFoundException
import com.ghrcosta.planningpoker.service.RoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Add new participant to a room",
        description = "Adds the given name as a participant in the given room (if it's unique) and returns a session " +
                      "object to be used by following requests.")
    fun addParticipant(
        @PathVariable("roomId") roomId: String,
        @RequestParam("name") participantName: String,
    ): SessionDTO = runBlocking {
        roomService.addParticipant(roomId = roomId, participantName = participantName)
        return@runBlocking SessionDTO(roomId = roomId, participantName = participantName)
    }

    @PutMapping("/{roomId}/vote")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Set participant vote",
        description = "Sets vote of a participant in the room. Both room and participant are retrieved from session.")
    fun setParticipantVote(
        @PathVariable("roomId") roomId: String,
        @RequestParam("value") vote: String,
        @RequestBody session: SessionDTO,
    ) = runBlocking {
        validateSession(session, roomId)
        roomService.setParticipantVote(roomId = roomId, participantName = session.participantName, vote = vote)
    }

    @PostMapping("/{roomId}/revealVotes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Reveal votes in the room",
        description = "Reveals votes from everyone in the room. Only someone in the room can execute it. " +
                      "Both room and participant are retrieved from session.")
    fun revealVotes(
        @PathVariable("roomId") roomId: String,
        @RequestBody session: SessionDTO,
    ) = runBlocking {
        validateSession(session, roomId)
        roomService.revealVotes(roomId = roomId, participantName = session.participantName)
    }

    @PostMapping("/{roomId}/clearVotes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete votes in the room",
        description = "Remove votes from everyone in the room and sets vote visibility to hidden. " +
                      "Only someone in the room can execute it. " +
                      "Both room and participant are retrieved from session.")
    fun clearVotes(
        @PathVariable("roomId") roomId: String,
        @RequestBody session: SessionDTO,
    ) = runBlocking {
        validateSession(session, roomId)
        roomService.clearVotes(roomId = roomId, participantName = session.participantName)
    }

    private fun validateSession(session: SessionDTO, roomId: String) {
        if (session.roomId.isBlank() || session.participantName.isBlank()) {
            throw IllegalArgumentException("Invalid session")
        }
        if (roomId != session.roomId) {
            throw IllegalArgumentException("Session mismatch")
        }
    }

    @ExceptionHandler(RoomNotFoundException::class)
    private fun handleRoomNotFoundException(e: RoomNotFoundException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DuplicatedParticipantNameException::class)
    private fun handleDuplicatedParticipantNameException(e: DuplicatedParticipantNameException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ParticipantNotFoundException::class)
    private fun handleParticipantNotFoundException(e: ParticipantNotFoundException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    private fun handleParticipantNotFoundException(e: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
    }
}