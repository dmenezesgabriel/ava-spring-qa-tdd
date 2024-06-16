package com.example.api.performance;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class PerformanceSimulation extends Simulation {
    private final HttpProtocolBuilder httpProtocolBuilder = http.baseUrl("http://localhost:8080")
            .header("Content-Type", "application/json");

    ActionBuilder addMessageRequest = http("request: add message")
            .post("/messages")
            .body(StringBody("{ \"username\": \"John\", \"content\": \"Hello, World!\" }"))
            .check(status().is(201))
            .check(jsonPath("$.id").saveAs("messageId"));

    ActionBuilder getMessageRequest = http("request: get message")
            .get("/messages/#{messageId}")
            .check(status().is(200));

    ActionBuilder deleteMessageRequest = http("request: delete message")
            .delete("/messages/#{messageId}")
            .check(status().is(200));


    ScenarioBuilder scenarioBuilderAddMessage = scenario("Add message")
            .exec(addMessageRequest);

    ScenarioBuilder scenarioBuilderGetMessage = scenario("Get message")
            .exec(addMessageRequest)
            .exec(getMessageRequest);

    ScenarioBuilder scenarioBuilderDeleteMessage = scenario("Delete message")
            .exec(addMessageRequest)
            .exec(deleteMessageRequest);

    {
        setUp(
                scenarioBuilderAddMessage.injectOpen(
                    rampUsersPerSec(1)
                            .to(2)
                            .during(Duration.ofSeconds(10)),
                    constantUsersPerSec(2)
                            .during(Duration.ofSeconds(20)),
                    rampUsersPerSec(2)
                            .to(1)
                            .during(Duration.ofSeconds(10))
                ),
                scenarioBuilderGetMessage.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))
                ),
                scenarioBuilderDeleteMessage.injectOpen(
                        rampUsersPerSec(1)
                                .to(5)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(5)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(5)
                                .to(1)
                                .during(Duration.ofSeconds(10))
                )
        )
            .protocols(httpProtocolBuilder)
            .assertions(
                    global().responseTime().max().lt(5000)
            );
    }
}
