import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

public class ApiHomeTaskWorkshop extends Simulation {

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

    private static final String UPDATE_GAME_BODY = """
                {
                  "title": "REMATCH",
                  "developer": "Sloclap",
                  "publisher": "Sloclap, Kepler Interactive",
                  "releaseYear": 2025,
                  "genre": "Soccer",
                  "platform": "PC, XBOX, PS5",
                  "price": 30.12
                }
                """;

    private static final String CREATE_SECOND_GAME_BODY = """
                {
                  "title": "Counter-Strike 2",
                  "developer": "Valve",
                  "publisher": "Valvee",
                  "releaseYear": 2022,
                  "genre": "FPS, Shooter, Multiplayer, Competitive",
                  "platform": "PC",
                  "price": 13.29
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

    //2. Create VideoGame (REMATCH)
    private final ChainBuilder createVideoGame = exec(
            http("Create Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("firstGameId"))
    );
    //3. Update the VideoGame
    private final ChainBuilder updateVideoGame = exec(
            http("Update Video Game")
                    .put("/videogames/#{firstGameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(UPDATE_GAME_BODY))
                    .check(status().is(200))
    );

    //4. Create second VideoGame (Counter Strike 2)
    private final ChainBuilder createSecondVideoGame = exec(
            http("Create Second Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_SECOND_GAME_BODY))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("secondGameId"))
    );
    //6. Delete the second VideoGame by gameID
    private final ChainBuilder deleteSecondGame = exec(
            http("Delete second Video Game")
                    .delete("/videogames/#{secondGameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.status").is("success"))
                    .check(jsonPath("$.data").isNull())
    );

    // Scenario definition
    ScenarioBuilder scn = scenario("Home-Task Workshop Test")
            .exec(loginUser)
            .pause(1)
            .exec(createVideoGame)
            .pause(1)
            .exec(updateVideoGame)
            .pause(1)
            .exec(createSecondVideoGame)
            .pause(1)
            .exec(deleteSecondGame)
            .pause(1);
        {
    // Set up the simulation with the scenario
    setUp(
            scn.injectOpen(
                    rampUsers(25).during(Duration.ofMinutes(5))
            )
    ).protocols(httpProtocol);
        }
}