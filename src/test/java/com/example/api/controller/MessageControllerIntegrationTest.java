package com.example.api.controller;

import com.example.api.model.Message;
import com.example.api.utils.MessageHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
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
                    // .log().all()
                    .when()
                    .post("/messages")
                    .then()
                    // .log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/MessageSchema.json"));

        }

        @Test
        void shouldThrowExceptionWhenRegisterIfMessagePayloadIsTypeXML() {
            String xmlPayload = """
                <message><username>Name</username><content>Hello!</content></message>
                """;
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
                    .when()
                    .post("/messages")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("$", hasKey("timestamp"))
                    .body("$", hasKey("status"))
                    .body("$", hasKey("error"))
                    .body("$", hasKey("path"))
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/messages"));
        }

    }

    @Nested
    class GetMessage {
        @Test
        void shouldAllowGetMessage() {
            var id = "cf9f5083-c5fb-4061-91cf-cd80eec30c89";

            when()
                .get("/messages/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value());
        }

        @Test
        void shouldThrowExceptionWhenGetIfMessageIdNotFound() {
            var id = UUID.randomUUID();
            when()
                    .get("/messages/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class UpdateMessage {
        @Test
        void shouldAllowUpdateMessage() {
            var id = UUID.fromString("450fb656-3b0a-4044-9976-06e04d2df74e");
            var timestamp = LocalDateTime.now();
            var message = Message.builder()
                    .id(id)
                    .username("Adam")
                    .content("Hello, World! 04")
                    .updatedAt(timestamp)
                    .build();
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(message)
                    .when()
                    .put("/messages/{id}", id)
                    .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/MessageSchema.json"));
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdNotFound() {
            var id = UUID.randomUUID();
            var timestamp = LocalDateTime.now();
            var message = Message.builder()
                    .id(id)
                    .username("Someone")
                    .content("Hello, World! x")
                    .updatedAt(timestamp)
                    .build();
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(message)
                    .when()
                    .put("/messages/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Message not found"));
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdIsNotEqual() {
            var id = UUID.fromString("450fb656-3b0a-4044-9976-06e04d2df74");
            var timestamp = LocalDateTime.now();
            var message = Message.builder()
                    .id(id)
                    .username("Someone")
                    .content("Hello, World! x")
                    .updatedAt(timestamp)
                    .build();
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(message)
                    .when()
                    .put("/messages/{id}", "450fb656-3b0a-4044-9976-06e04d2df74e")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Updated message does not have the correct ID"));
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessagePayloadIsTypeXML() {
            var id = UUID.fromString("450fb656-3b0a-4044-9976-06e04d2df74e");
            String xmlPayload = """
                <message><username>Name</username><content>Hello!</content></message>
                """;
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
                    .when()
                    .put("/messages/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/messages/450fb656-3b0a-4044-9976-06e04d2df74e"));
        }
    }

    @Nested
    class DeleteMessage {
        @Test
        void shouldAllowDeleteMessage() {
            var id = UUID.fromString("f4158f3e-7d31-45e9-84bd-19fdea672af3");
            when()
                    .delete("/messages/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo("Message deleted"));
        }

        @Test
        void shouldThrowExceptionWhenDeleteIfMessageIdNotFound() {
            var id = UUID.randomUUID();
            when()
                    .delete("/messages/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Message not found"));
        }
    }

    @Nested
    class ListMessages {
        @Test
        void shouldAllowListMessages() {
            given()
                    .queryParam("page", "0")
                    .queryParam("size", "10")
                    .when()
                    .get("/messages")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/MessagePaginationSchema.json"));
        }

        @Test
        void shouldAllowListMessagesWhenPaginationParamsNotInformed() {
            given()
                    .when()
                    .get("/messages")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/MessagePaginationSchema.json"));
        }
    }
}
