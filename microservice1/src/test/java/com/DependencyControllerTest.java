package com;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import com.example.microservice1.controller.DependencyController;
import com.example.microservice1.service.DependencyUpdateService;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class DependencyControllerTest {

    @InjectMocks
    private DependencyController dependencyController;

    @Mock
    private DependencyUpdateService dependencyUpdateService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dependencyController).build();
    }

    @Test
    public void testUpdateDependencies() throws Exception {
        doNothing().when(dependencyUpdateService).updateDependencies(anyString());

        mockMvc.perform(get("/update/microservice2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Dependencies updated successfully for microservice: microservice2"));

        verify(dependencyUpdateService, times(1)).updateDependencies(anyString());
    }

    @Test
    public void testUpdateDependenciesException() throws Exception {
        doThrow(new IOException("Test Exception")).when(dependencyUpdateService).updateDependencies(anyString());

        mockMvc.perform(get("/update/microservice2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Failed to update dependencies for microservice: microservice2. Check logs for details."));

        verify(dependencyUpdateService, times(1)).updateDependencies(anyString());
    }
}
