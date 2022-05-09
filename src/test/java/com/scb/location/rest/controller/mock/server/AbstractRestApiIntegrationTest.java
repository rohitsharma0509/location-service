package com.scb.location.rest.controller.mock.server;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@SpringBootTest
public class AbstractRestApiIntegrationTest {

    private static ClientAndServer mockServer;

    @BeforeClass
    public static void startMockServer() {
        mockServer = startClientAndServer(1080);
        BaseMockSetup.setUp(mockServer);
    }

    @AfterClass
    public static void stopMockServer() {
        if (mockServer.isRunning()) {
            mockServer.stop();
        }
    }

    @AfterEach
    void clean() {
        if (mockServer.isRunning()) {
            mockServer.stop();
        }
    }

    @BeforeEach
    void setup() {

        mockServer = startClientAndServer(1080);
        BaseMockSetup.setUp(mockServer);
    }

    }
