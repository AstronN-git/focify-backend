package org.astron.focify_backend.controller;

import org.astron.focify_backend.api.controller.PingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PingController.class)
public class PingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPing() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk());
    }
}
