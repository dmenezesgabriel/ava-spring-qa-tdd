package com.example.api.bdd;

import com.example.api.model.Message;
import com.example.api.utils.MessageHelper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class StepDefinition {

    private Response response;

    private Message responseMessage;

    private final String API_MESSAGES_ENDPOINT = "http://localhost:8080/messages";

    @When("a new message is submitted")
    public void a_new_message_is_submitted() {
     var messageRequest = MessageHelper.createMessage();
     response = given()
             .contentType(MediaType.APPLICATION_JSON_VALUE)
             .body(messageRequest)
             .when()
             .post(API_MESSAGES_ENDPOINT);
    }

    @Then("the message should be registered with success")
    public void the_message_should_be_registered_with_success() {
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/MessageSchema.json"));
    }

    @Then("it should be presented")
    public void it_should_be_presented() {
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/MessageSchema.json"));
    }
}
