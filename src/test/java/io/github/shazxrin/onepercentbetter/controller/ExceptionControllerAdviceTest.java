package io.github.shazxrin.onepercentbetter.controller;

import io.github.shazxrin.onepercentbetter.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExceptionControllerAdviceTest.ExceptionController.class)
@Import(ExceptionControllerAdviceTest.ExceptionController.class)
public class ExceptionControllerAdviceTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHandleBadRequestException_shouldReturnBadRequestStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/test/bad-request"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.type").exists())
            .andExpect(jsonPath("$.detail").value("Bad request."));
    }

    @Test
    void testNormalEndpoint_shouldReturnOkStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/test/ok"))
            .andExpect(status().isOk())
            .andExpect(content().string("OK"));
    }

    @RestController
    static class ExceptionController {
        @GetMapping("/test/bad-request")
        public String badRequest() {
            throw new BadRequestException("Bad request.");
        }

        @GetMapping("/test/ok")
        public String ok() {
            return "OK";
        }
    }
}
