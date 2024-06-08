package com.example.api.controller;

import com.example.api.utils.MessageHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class RegisterMessage {
        @Test
        void shouldAllowRegisterMessage() {
            var message = MessageHelper.createMessage();
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(message)
                    .when()
                    .post("/messages")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
        }

        @Test
        void shouldThrowExceptionWhenRegisterIfMessagePayloadIsTypeXML() {
            fail("NotImplementedError");
        }
    }

    @Nested
    class GetMessage {
        @Test
        void shouldAllowGetMessage() {
            fail("NotImplementedError");
        }

        @Test
        void shouldThrowExceptionWhenGetIfMessageIdNotFound() {
            fail("NotImplementedError");
        }
    }

    @Nested
    class UpdateMessage {
        @Test
        void shouldAllowUpdateMessage() {
            fail("NotImplementedError");
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdNotFound() {
            fail("NotImplementedError");
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdIsNotEqual() {
            fail("NotImplementedError");
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessagePayloadIsTypeXML() {
            fail("NotImplementedError");
        }
    }

    @Nested
    class DeleteMessage {
        @Test
        void shouldAllowDeleteMessage() {
            fail("NotImplementedError");
        }

        @Test
        void shouldThrowExceptionWhenDeleteIfMessageIdNotFound() {
            fail("NotImplementedError");
        }
    }

    @Nested
    class ListMessages {
        @Test
        void shouldAllowListMessages() {
            fail("NotImplementedError");
        }

        @Test
        void shouldAllowListMessagesWhenPaginationParamsNotInformed() {
            fail("NotImplementedError");
        }
    }
}
