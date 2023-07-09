package com.ghrcosta.planningpoker.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/_ah")
@Tag(name = "gaeApi", description = "API used internally by GCP App Engine")
class AppEngineController {
    /**
     * Endpoint called by GAE when deployed with Basic scaling.
     * Must return 200-299 or 404, otherwise GAE will restart the application.
     * See: [GAE docs](https://cloud.google.com/appengine/docs/standard/java-gen2/how-instances-are-managed#startup)
     */
    @GetMapping("/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Instance startup",
        description = "Called by App Engine during container initialization.")
    @ApiResponse(
        responseCode = "204",
        description = "Application is up and listening to requests")
    fun appEngineWarmup() { /* Nothing to do */ }
}