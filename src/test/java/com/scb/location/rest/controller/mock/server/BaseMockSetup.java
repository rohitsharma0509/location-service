package com.scb.location.rest.controller.mock.server;

import org.mockserver.integration.ClientAndServer;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class BaseMockSetup {
    public static void setUp(ClientAndServer mockServer) {
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/api/distance")
                .withQueryStringParameter("longitudeFrom","100.537953" )
                .withQueryStringParameter("latitudeFrom","13.753007" )
                .withQueryStringParameter("longitudeTo","100.23" )
                .withQueryStringParameter("latitudeTo","13.54"))
                .respond(
                        response()
                                .withBody("{\n" +
                                        "    \"longitudeFrom\": 100.537953,\n" +
                                        "    \"latitudeFrom\": 13.753007,\n" +
                                        "    \"longitudeTo\": 100.23,\n" +
                                        "    \"latitudeTo\": 13.54,\n" +
                                        "    \"distance\": 62514.0,\n" +
                                        "    \"duration\": 3316.0\n" +
                                        "}")
                                .withStatusCode(400)
                                .withHeader("Content-Type", "application/json"));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/api/distance")
                .withQueryStringParameter("longitudeFrom","100.537953" )
                .withQueryStringParameter("latitudeFrom","13.753007" )
                .withQueryStringParameter("longitudeTo","100.392120" )
                .withQueryStringParameter("latitudeTo","13.268905"))
                .respond(
                        response()
                                .withBody("{\n" +
                                        "    \"errorCode\": 404,\n" +
                                        "    \"errorMessage\": \"No Routes Exists\"\n" +
                                        "}")
                                .withStatusCode(400)
                                .withHeader("Content-Type", "application/json"));
    }
}
