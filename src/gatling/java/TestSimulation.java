import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class TestSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://httpbin.org") // Here is the base URL
            .acceptHeader("application/json");

    // Scenario definition
    ScenarioBuilder scn = scenario("Example Scenario")
            // GET request example
            .exec(http("GET Request")
                    .get("/get")
                    .check(status().is(200)));

    {
        // Set up the simulation with the scenario
        setUp(
                scn.injectOpen(atOnceUsers(1)) // Define the injection profile, here it's 1 user at once
        ).protocols(httpProtocol);
    }

}
