package com.ghrcosta.planningpoker.dto

import com.ghrcosta.planningpoker.entity.Participant

data class ParticipantDTO(
    val name: String,
    val vote: String? = null,
) {
    companion object {
        fun Participant.dto(): ParticipantDTO {
            return ParticipantDTO(
                name = name,
                vote = vote
            )
        }
    }
}
