import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class ApiSimulationWorkshop extends Simulation {
    //Set up http protocol
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final String LOGIN_BODY = """
            {
             "username": "testricardo",
             "password": "testricardo"
            }
            """;

    private static final String CREATE_GAME_BODY = """
            {
              "title": "REMATCH",
              "developer": "Sloclap",
              "publisher": "Sloclap, Kepler Interactive",
              "releaseYear": 2025,
              "genre": "Soccer",
              "platform": "PC, XBOX, PS5",
              "price": 35.99
            }
            """;

    //1. Login User
    private final ChainBuilder loginUser = exec(
            http("Login User")
                    .post("/auth/login")
                    .body(StringBody(LOGIN_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data").saveAs("authToken"))
    );

    //2. Get all VideoGames
    private final ChainBuilder getAllVideoGames = exec(
            http("Get All VideoGames ")
                    .get("/videogames")
                    .check(status().is(200))
    );

    //3. Create VideoGame
    private final ChainBuilder createVideoGame = exec(
            http("Create Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameId"))
    );

    //4. Get VideoGame
    private final ChainBuilder getVideoGame = exec(
            http("Get Video Game")
                    .get("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("REMATCH"))
    );

    // Scenario definition
    ScenarioBuilder scn = scenario("VideoGame Test")
            .exec(loginUser)
            .pause(1)
            .exec(getAllVideoGames)
            .pause(1)
            .exec(createVideoGame)
            .pause(1)
            .exec(getVideoGame);
    {
        // Set up the simulation with the scenario
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}