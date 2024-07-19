import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class TestSimulation extends Simulation {
    //Set up http protocol
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://httpbin.org")
            .acceptHeader("application/json");

    // Scenario definition
    ScenarioBuilder scn = scenario("Example Scenario")
            // GET request example
            .exec(http("GET Request")
                    .get("/get")
                    .queryParam("param", "something")
                    .check( status().is(200),
                            bodyString().exists()));

    {
        // Set up the simulation with the scenario
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }

}
