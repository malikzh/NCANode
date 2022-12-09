package kz.ncanode.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import java.nio.charset.StandardCharsets

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

abstract class IntegrationSpecification extends Specification implements WithTestData {

    protected MockMvc mockMvc

    def configureMockMvc(Object controller) {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    protected <T> T doPostQuery(String uri, String json, int expectedStatus, Class<T> clazz) {
        def response = mockMvc.perform(
            post(uri).contentType(MediaType.APPLICATION_JSON).content(json)
        ).andDo(MockMvcResultHandlers.print()).andExpect(status().is(expectedStatus))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8)

        return new ObjectMapper().readValue(response, clazz)
    }
}
