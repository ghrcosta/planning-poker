package com.ghrcosta.planningpoker.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ghrcosta.planningpoker.dto.RoomDTO
import jakarta.servlet.http.Cookie
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
import kotlin.test.assertTrue

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
        fun addParticipantReturnsNoContentAndSetsCookies() {
            val requestResult =
                mockMvc
                    .perform(
                        put("/${room.id}/addParticipant")
                            .param("name", aPersonName))
                    .andExpect(status().isNoContent)
                    .andReturn()

            val cookies = requestResult.response.cookies
            assertTrue { cookies.any { it.name == COOKIE_ROOM } }
            assertTrue { cookies.any { it.name == COOKIE_PARTICIPANT && it.value == aPersonName } }
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
        private lateinit var cookies: Array<Cookie>
        private val aPersonName = "person"

        @BeforeTest
        fun beforeEachTest() {
            // Create test room
            val createRequestJsonResult = mockMvc.perform(post("/create")).andReturn().response.contentAsString
            room = jacksonObjectMapper().readValue<RoomDTO>(createRequestJsonResult)

            // Add test participant and get the cookies required for further requests
            cookies =
                mockMvc
                    .perform(put("/${room.id}/addParticipant").param("name", aPersonName))
                    .andReturn().response.cookies
        }

        @Test
        fun voteReturnsNoContent() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .param("value", "1")
                        .cookie(*cookies))
                .andExpect(status().isNoContent)
        }

        @Test
        fun voteWithoutValueReturnsBadRequest() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .cookie(*cookies))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun voteToInvalidParticipantReturnsNotFound() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .param("value", "1")
                        .cookie(
                            Cookie(COOKIE_ROOM, room.id),
                            Cookie(COOKIE_PARTICIPANT, "invalidParticipant")))
                .andExpect(status().isNotFound)
        }

        @Test
        fun voteToInvalidRoomReturnsNotFound() {
            mockMvc
                .perform(
                    put("/roomThatDoesNotExist/vote")
                        .param("value", "1")
                        .cookie(
                            Cookie(COOKIE_ROOM, "roomThatDoesNotExist"),
                            Cookie(COOKIE_PARTICIPANT, aPersonName)))
                .andExpect(status().isNotFound)
        }

        @Test
        fun voteToWrongRoomReturnsBadRequest() {
            mockMvc
                .perform(
                    put("/aDifferentRoomFromCookie/vote")
                        .param("value", "1")
                        .cookie(*cookies))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun voteWithoutRoomIdCookieReturnsBadRequest() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .param("value", "1")
                        .cookie(*cookies.filterNot { it.name == COOKIE_ROOM }.toTypedArray()))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun voteWithoutParticipantCookieReturnsBadRequest() {
            mockMvc
                .perform(
                    put("/${room.id}/vote")
                        .param("value", "1")
                        .cookie(*cookies.filterNot { it.name == COOKIE_PARTICIPANT }.toTypedArray()))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun voteWithoutCookiesReturnsBadRequest() {
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
        private lateinit var cookies: Array<Cookie>
        private val aPersonName = "person"

        @BeforeTest
        fun beforeEachTest() {
            // Create test room
            val createRequestJsonResult = mockMvc.perform(post("/create")).andReturn().response.contentAsString
            room = jacksonObjectMapper().readValue<RoomDTO>(createRequestJsonResult)

            // Add test participant and get the cookies required for further requests
            cookies =
                mockMvc
                    .perform(put("/${room.id}/addParticipant").param("name", aPersonName))
                    .andReturn().response.cookies
        }

        @Test
        fun revealVotesReturnsNoContent() {
            mockMvc
                .perform(
                    post("/${room.id}/revealVotes")
                        .cookie(*cookies))
                .andExpect(status().isNoContent)
        }

        @Test
        fun revealVotesWithInvalidParticipantReturnsNotFound() {
            mockMvc
                .perform(
                    post("/${room.id}/revealVotes")
                        .cookie(
                            Cookie(COOKIE_ROOM, room.id),
                            Cookie(COOKIE_PARTICIPANT, "invalidParticipant")))
                .andExpect(status().isNotFound)
        }

        @Test
        fun revealVotesWithInvalidRoomReturnsNotFound() {
            mockMvc
                .perform(
                    post("/roomThatDoesNotExist/revealVotes")
                        .cookie(
                            Cookie(COOKIE_ROOM, "roomThatDoesNotExist"),
                            Cookie(COOKIE_PARTICIPANT, aPersonName)))
                .andExpect(status().isNotFound)
        }

        @Test
        fun revealVotesToWrongRoomReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/aDifferentRoomFromCookie/revealVotes")
                        .cookie(*cookies))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun revealVotesWithoutRoomIdCookieReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/${room.id}/revealVotes")
                        .cookie(*cookies.filterNot { it.name == COOKIE_ROOM }.toTypedArray()))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun revealVotesWithoutParticipantCookieReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/${room.id}/revealVotes")
                        .cookie(*cookies.filterNot { it.name == COOKIE_PARTICIPANT }.toTypedArray()))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun revealVotesWithoutCookiesReturnsBadRequest() {
            mockMvc
                .perform(post("/${room.id}/revealVotes"))
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class ClearVotesTest {
        private lateinit var room: RoomDTO
        private lateinit var cookies: Array<Cookie>
        private val aPersonName = "person"

        @BeforeTest
        fun beforeEachTest() {
            // Create test room
            val createRequestJsonResult = mockMvc.perform(post("/create")).andReturn().response.contentAsString
            room = jacksonObjectMapper().readValue<RoomDTO>(createRequestJsonResult)

            // Add test participant and get the cookies required for further requests
            cookies =
                mockMvc
                    .perform(put("/${room.id}/addParticipant").param("name", aPersonName))
                    .andReturn().response.cookies
        }

        @Test
        fun clearVotesReturnsNoContent() {
            mockMvc
                .perform(
                    post("/${room.id}/clearVotes")
                        .cookie(*cookies))
                .andExpect(status().isNoContent)
        }

        @Test
        fun clearVotesWithInvalidParticipantReturnsNotFound() {
            mockMvc
                .perform(
                    post("/${room.id}/clearVotes")
                        .cookie(
                            Cookie(COOKIE_ROOM, room.id),
                            Cookie(COOKIE_PARTICIPANT, "invalidParticipant")))
                .andExpect(status().isNotFound)
        }

        @Test
        fun clearVotesWithInvalidRoomReturnsNotFound() {
            mockMvc
                .perform(
                    post("/roomThatDoesNotExist/clearVotes")
                        .cookie(
                            Cookie(COOKIE_ROOM, "roomThatDoesNotExist"),
                            Cookie(COOKIE_PARTICIPANT, aPersonName)))
                .andExpect(status().isNotFound)
        }

        @Test
        fun clearVotesToWrongRoomReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/aDifferentRoomFromCookie/clearVotes")
                        .cookie(*cookies))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun clearVotesWithoutRoomIdCookieReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/${room.id}/clearVotes")
                        .cookie(*cookies.filterNot { it.name == COOKIE_ROOM }.toTypedArray()))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun clearVotesWithoutParticipantCookieReturnsBadRequest() {
            mockMvc
                .perform(
                    post("/${room.id}/clearVotes")
                        .cookie(*cookies.filterNot { it.name == COOKIE_PARTICIPANT }.toTypedArray()))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun clearVotesWithoutCookiesReturnsBadRequest() {
            mockMvc
                .perform(post("/${room.id}/clearVotes"))
                .andExpect(status().isBadRequest)
        }
    }

    companion object {
        private const val COOKIE_ROOM = "roomId"
        private const val COOKIE_PARTICIPANT = "participant"
    }
}