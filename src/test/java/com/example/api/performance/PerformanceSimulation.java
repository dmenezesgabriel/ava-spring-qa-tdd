package com.example.api.performance;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class PerformanceSimulation extends Simulation {
    private final HttpProtocolBuilder httpProtocolBuilder = http.baseUrl("http://localhost:8080")
            .header("Content-Type", "application/json");

    ActionBuilder addRequestMessage = http("add message")
            .post("/messages")
            .body(StringBody("{ \"username\": \"John\", \"content\": \"Hello, World!\" }"))
            .check(status().is(201));

    ScenarioBuilder scenarioBuilderAddMessage = scenario("Add message")
            .exec(addRequestMessage);

    {
        setUp(scenarioBuilderAddMessage.injectOpen(
            rampUsersPerSec(1)
                    .to(10)
                    .during(Duration.ofSeconds(10)),
            constantUsersPerSec(10)
                    .during(Duration.ofSeconds(20)),
            rampUsersPerSec(10)
                    .to(1)
                    .during(Duration.ofSeconds(10))

            )
        )
            .protocols(httpProtocolBuilder)
            .assertions(
                    global().responseTime().max().lt(300)
            );
    }
}
