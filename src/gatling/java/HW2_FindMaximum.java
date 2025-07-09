import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class HW2_FindMaximum extends Simulation {

    private static final String LOGIN_BODY =
            """
            {
               "username": "mrangelov",
                "password": "Qw3rty"
            }""";

    //Set up http protocol
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private final ChainBuilder loginUser = exec(
            http("Login User")
                    .post("/auth/login")
                    .body(StringBody(LOGIN_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data").saveAs("authToken"))
    );

    private final ScenarioBuilder scn = scenario("Add 5 users per minute until 25")
            .pause(1)
                    .exec(loginUser);

    {
        // Set up the simulation with the scenario
        setUp(
                scn.injectOpen(rampUsers(25).during(300))
        ).protocols(httpProtocol);
    }
}
