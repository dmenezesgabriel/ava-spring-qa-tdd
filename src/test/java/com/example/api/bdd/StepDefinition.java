package com.example.api.bdd;

import com.example.api.model.Message;
import com.example.api.utils.MessageHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class StepDefinition {

    private Response response;

    private Message responseMessage;

    private final String API_MESSAGES_ENDPOINT = "http://localhost:8080/messages";

    @When("a new message is submitted")
    public Message a_new_message_is_submitted() {
     var messageRequest = MessageHelper.createMessage();
     response = given()
             .contentType(MediaType.APPLICATION_JSON_VALUE)
             .body(messageRequest)
             .when()
             .post(API_MESSAGES_ENDPOINT);

        return response.then().extract().as(Message.class);
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

    @Given("a message was already submitted")
    public void a_message_was_already_submitted() {
        responseMessage = a_new_message_is_submitted();
    }

    @When("the message is searched")
    public void the_message_is_searched() {
        response  = when()
                .get(API_MESSAGES_ENDPOINT + "/{id}", responseMessage.getId());
    }

    @Then("is successfully presented")
    public void is_successfully_presented() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/MessageSchema.json"));
    }

    @When("an updated request is made")
    public void a_updated_request_is_made() {
        responseMessage.setContent("Hello, changing this message content!");
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage)
                .when()
                .put(API_MESSAGES_ENDPOINT + "/{id}", responseMessage.getId());


    }
    @Then("the message is successfully updated")
    public void the_message_is_successfully_updated() {
        // Write code here that turns the phrase above into concrete actions
        response.then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/MessageSchema.json"));
    }

    @When("a remove request is made")
    public void a_remove_request_is_made() {
        response = when()
                .delete(API_MESSAGES_ENDPOINT + "/{id}", responseMessage.getId());
    }
    @Then("the message is successfully deleted")
    public void the_message_is_successfully_deleted() {
        response.then()
                .statusCode(HttpStatus.OK.value());
    }
}
