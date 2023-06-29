package com.ghrcosta.planningpoker.controller

import kotlin.test.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AppEngineControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun startReturnsSuccess() {
        mockMvc
            .perform(get("/_ah/start"))
            .andExpect(status().is2xxSuccessful)
    }
}