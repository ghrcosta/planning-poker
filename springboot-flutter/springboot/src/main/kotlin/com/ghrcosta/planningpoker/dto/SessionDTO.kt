package com.ghrcosta.planningpoker.dto

import jakarta.validation.constraints.NotBlank

data class SessionDTO(
    @field:NotBlank(message = "Room ID is required")
    val roomId: String,

    @field:NotBlank(message = "Participant name is required")
    val participantName: String,
)