package com.ghrcosta.planningpoker.controller

import com.ghrcosta.planningpoker.service.RoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/task")
@Tag(name = "taskApi", description = "API used internally by GCP App Engine Cron Jobs")
class TaskController(private val roomService: RoomService) {

    /**
     * Endpoint called by a GAE Cron Job.
     */
    @GetMapping("/cleanup")  // Must be GET because it's called automatically by GCP
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Clear database",
        description = "Called periodically by a GCP App Engine Cron Job to remove all rooms from the database.")
    @ApiResponse(
        responseCode = "204",
        description = "Database cleared")
    fun cleanStorage() = runBlocking {
        roomService.deleteAllRooms()
    }
}