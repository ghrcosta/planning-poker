package com.ghrcosta.planningpoker.dto

import com.ghrcosta.planningpoker.dto.ParticipantDTO.Companion.dto
import com.ghrcosta.planningpoker.entity.Room
import io.swagger.v3.oas.annotations.media.Schema

data class RoomDTO(
    val id: String,
    val participants: List<ParticipantDTO>,

    @Schema(description = "If 'true', client must display participant votes")
    val votesRevealed: Boolean,
) {
    companion object {
        fun Room.dto(): RoomDTO {
            return RoomDTO(
                id = id,
                participants = participants.map { it.dto() },
                votesRevealed = votesRevealed,
            )
        }
    }
}
