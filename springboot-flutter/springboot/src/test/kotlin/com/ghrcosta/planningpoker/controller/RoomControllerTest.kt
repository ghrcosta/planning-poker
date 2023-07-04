package com.ghrcosta.planningpoker.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ghrcosta.planningpoker.dto.RoomDTO
import com.ghrcosta.planningpoker.dto.SessionDTO
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.BeforeTest
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun createReturnsNewRoom() {
        mockMvc
            .perform(post("/create"))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isString)
            .andExpect(jsonPath("$.participants").isArray)
            .andExpect(jsonPath("$.participants").isEmpty)
            .andExpect(jsonPath("$.votesRevealed").isBoolean)
    }

    @Nested
    inner class AddParticipantTest {
        private lateinit var room: RoomDTO
        private val aPersonName = "person"

        @BeforeTest
        fun beforeEachTest() {
            // Create test room
            val createRequestJsonResult = mockMvc.perform(post("/create")).andReturn().response.contentAsString
            room = jacksonObjectMapper().readValue<RoomDTO>(createRequestJsonResult)
        }

        @Test
        fun addParticipantReturnsCreatedSession() {
            mockMvc
                .perform(
                    put("/${room.id}/addParticipant")
                        .param("name", aPersonName))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.roomId").value(room.id))
                .andExpect(jsonPath("$.participantName").value(aPersonName))
        }

        @Test
        fun addParticipantWithoutNameReturnsBadRequest() {
            mockMvc
                .perform(put("/${room.id}/addParticipant"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun addParticipantToInvalidRoomReturnsNotFound() {
            mockMvc
                .perform(
                    put("/roomThatDoesNotExist/addParticipant")
                        .param("name", aPersonName))
                .andExpect(status().isNotFound)
        }

        @Test
        fun addParticipantDuplicatedReturnsConflict() {
            mockMvc
                .perform(
                    put("/${room.id}/addParticipant")
                        .param("name", aPersonName))

            mockMvc
                .perform(
                    put("/${room.id}/addParticipant")
                        .param("name", aPersonName))
                .andExpect(status().isConflict)
        }
    }

    @Nested
    inner class VoteTest {
        private lateinit var room: RoomDTO
        private lateinit var session: SessionDTO
        private val aPersonName = "person"

        @BeforeTest
        fun beforeEachTest() {
            // Create test room
            val createRequestJsonResult = mockMvc.perform(post("/create")).andReturn().response.contentAsString
            room = jacksonObjectMapper().readValue<RoomDTO>(createRequestJsonResult)

            // Add test participant and get the session required for further requests
            val addParticipantJsonResult =
                mockMvc
                    .perform(put("/${room.id}/addParticipant").param("name", aPersonName))
                    .andReturn().response.contentAsString
            session = jacksonObjectMapper().readValue<SessionDTO>(addParticipantJsonResult)
        }

        @Test
        fun voteReturnsNoContent() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .param("value", "1")
                        .content(jacksonObjectMapper().writeValueAsString(session))
                        .contentType("application/json"))
                .andExpect(status().isNoContent)
        }

        @Test
        fun voteWithoutValueReturnsBadRequest() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .content(jacksonObjectMapper().writeValueAsString(session)))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun voteToInvalidParticipantReturnsNotFound() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .param("value", "1")
                        .content(jacksonObjectMapper().writeValueAsString(
                            session.copy(participantName = "invalidParticipant")))
                        .contentType("application/json"))
                .andExpect(status().isNotFound)
        }

        @Test
        fun voteToInvalidRoomReturnsNotFound() {
            mockMvc
                .perform(
                    put("/roomThatDoesNotExist/vote")
                        .param("value", "1")
                        .content(jacksonObjectMapper().writeValueAsString(
                            session.copy(roomId = "roomThatDoesNotExist")))
                        .contentType("application/json"))
                .andExpect(status().isNotFound)
        }

        @Test
        fun voteToWrongRoomReturnsBadRequest() {
            mockMvc
                .perform(
                    put("/aDifferentRoomFromSession/vote")
                        .param("value", "1")
                        .content(jacksonObjectMapper().writeValueAsString(session))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun voteWithoutRoomIdSessionReturnsBadRequest() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .param("value", "1")
                        .content(jacksonObjectMapper().writeValueAsString(session.copy(roomId = "")))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun voteWithoutParticipantSessionReturnsBadRequest() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .param("value", "1")
                        .content(jacksonObjectMapper().writeValueAsString(session.copy(participantName = "")))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun voteWithoutSessionReturnsBadRequest() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .param("value", "1"))
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class RevealVotesTest {
        private lateinit var room: RoomDTO
        private lateinit var session: SessionDTO
        private val aPersonName = "person"

        @BeforeTest
        fun beforeEachTest() {
            // Create test room
            val createRequestJsonResult = mockMvc.perform(post("/create")).andReturn().response.contentAsString
            room = jacksonObjectMapper().readValue<RoomDTO>(createRequestJsonResult)

            // Add test participant and get the session required for further requests
            val addParticipantJsonResult =
                mockMvc
                    .perform(put("/${room.id}/addParticipant").param("name", aPersonName))
                    .andReturn().response.contentAsString
            session = jacksonObjectMapper().readValue<SessionDTO>(addParticipantJsonResult)
        }

        @Test
        fun revealVotesReturnsNoContent() {
            mockMvc
                .perform(
                    post("/${room.id}/revealVotes")
                        .content(jacksonObjectMapper().writeValueAsString(session))
                        .contentType("application/json"))
                .andExpect(status().isNoContent)
        }

        @Test
        fun revealVotesWithInvalidParticipantReturnsNotFound() {
            mockMvc
                .perform(
                    post("/${room.id}/revealVotes")
                        .content(jacksonObjectMapper().writeValueAsString(
                            session.copy(participantName = "invalidParticipant")))
                        .contentType("application/json"))
                .andExpect(status().isNotFound)
        }

        @Test
        fun revealVotesWithInvalidRoomReturnsNotFound() {
            mockMvc
                .perform(
                    post("/roomThatDoesNotExist/revealVotes")
                        .content(jacksonObjectMapper().writeValueAsString(
                            session.copy(roomId = "roomThatDoesNotExist")))
                        .contentType("application/json"))
                .andExpect(status().isNotFound)
        }

        @Test
        fun revealVotesToWrongRoomReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/aDifferentRoomFromSession/revealVotes")
                        .content(jacksonObjectMapper().writeValueAsString(session))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun revealVotesWithoutRoomIdSessionReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/${room.id}/revealVotes")
                        .content(jacksonObjectMapper().writeValueAsString(session.copy(roomId = "")))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun revealVotesWithoutParticipantSessionReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/${room.id}/revealVotes")
                        .content(jacksonObjectMapper().writeValueAsString(session.copy(participantName = "")))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun revealVotesWithoutSessionReturnsBadRequest() {
            mockMvc
                .perform(post("/${room.id}/revealVotes"))
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class ClearVotesTest {
        private lateinit var room: RoomDTO
        private lateinit var session: SessionDTO
        private val aPersonName = "person"

        @BeforeTest
        fun beforeEachTest() {
            // Create test room
            val createRequestJsonResult = mockMvc.perform(post("/create")).andReturn().response.contentAsString
            room = jacksonObjectMapper().readValue<RoomDTO>(createRequestJsonResult)

            // Add test participant and get the session required for further requests
            val addParticipantJsonResult =
                mockMvc
                    .perform(put("/${room.id}/addParticipant").param("name", aPersonName))
                    .andReturn().response.contentAsString
            session = jacksonObjectMapper().readValue<SessionDTO>(addParticipantJsonResult)
        }

        @Test
        fun clearVotesReturnsNoContent() {
            mockMvc
                .perform(
                    post("/${room.id}/clearVotes")
                        .content(jacksonObjectMapper().writeValueAsString(session))
                        .contentType("application/json"))
                .andExpect(status().isNoContent)
        }

        @Test
        fun clearVotesWithInvalidParticipantReturnsNotFound() {
            mockMvc
                .perform(
                    post("/${room.id}/clearVotes")
                        .content(jacksonObjectMapper().writeValueAsString(
                            session.copy(participantName = "invalidParticipant")))
                        .contentType("application/json"))
                .andExpect(status().isNotFound)
        }

        @Test
        fun clearVotesWithInvalidRoomReturnsNotFound() {
            mockMvc
                .perform(
                    post("/roomThatDoesNotExist/clearVotes")
                        .content(jacksonObjectMapper().writeValueAsString(
                            session.copy(roomId = "roomThatDoesNotExist")))
                        .contentType("application/json"))
                .andExpect(status().isNotFound)
        }

        @Test
        fun clearVotesToWrongRoomReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/aDifferentRoomFromSession/clearVotes")
                        .content(jacksonObjectMapper().writeValueAsString(session))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun clearVotesWithoutRoomIdSessionReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/${room.id}/clearVotes")
                        .content(jacksonObjectMapper().writeValueAsString(session.copy(roomId = "")))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun clearVotesWithoutParticipantSessionReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/${room.id}/clearVotes")
                        .content(jacksonObjectMapper().writeValueAsString(session.copy(participantName = "")))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun clearVotesWithoutSessionReturnsBadRequest() {
            mockMvc
                .perform(post("/${room.id}/clearVotes"))
                .andExpect(status().isBadRequest)
        }
    }
}