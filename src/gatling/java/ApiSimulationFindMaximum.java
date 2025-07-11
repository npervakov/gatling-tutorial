import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

public class ApiSimulationFindMaximum extends Simulation {

    // Reuse login JSON payload
    private static final String LOGIN_BODY = """
            {
                "username": "testuserBatman",
                "password": "helloworld2"
            }
            """;

    // HTTP protocol setup
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // 1. Get all videogames
    private final ChainBuilder getAllVideoGames = exec(
            http("Get All VideoGames")
                    .get("/videogames")
                    .check(status().is(200))
    );

    // 2. Login and save auth token
    private final ChainBuilder loginUser = exec(
            http("Login User")
                    .post("/auth/login")
                    .body(StringBody(LOGIN_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data").saveAs("authToken"))
    );

    // JSON payload for creating the original game (Battlefield)
    private static final String CREATE_GAME_BODY_ORIGINAL = """
            {
                "title": "Battlefield",
                "developer": "DICE",
                "publisher": "EA",
                "releaseYear": 2021,
                "genre": "Shooter",
                "platform": "PC",
                "price": 59.99
            }
            """;

    // 3. Create original game (Battlefield)
    private final ChainBuilder createOriginalGame = exec(
            http("Create Original Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY_ORIGINAL))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("originalGameId"))
    );

    // JSON payload for updating the original game (Battlefield)
    private static final String UPDATE_ORIGINAL_GAME_BODY = """
            {
                "title": "Battlefield",
                "developer": "DICE",
                "publisher": "EA",
                "releaseYear": 2021,
                "genre": "Shooter",
                "platform": "PC",
                "price": 40.99
            }
            """;

    // 4. Update the original game
    private final ChainBuilder updateOriginalGame = exec(
            http("Update Original Game Price")
                    .put("/videogames/#{originalGameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(UPDATE_ORIGINAL_GAME_BODY))
                    .check(status().is(200))
    );

    // JSON payload for creating a second, different game
    private static final String CREATE_GAME_BODY_SECOND = """
            {
                "title": "Cyberpunk 2077",
                "developer": "CD Projekt",
                "publisher": "CD Projekt",
                "releaseYear": 2023,
                "genre": "RPG",
                "platform": "PC",
                "price": 39.99
            }
            """;

    // 5. Create second, different game
    private final ChainBuilder createSecondGame = exec(
            http("Create Second Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY_SECOND))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("secondGameId"))
    );

    // 6. Delete the second game by its gameId
    private final ChainBuilder deleteSecondGame = exec(
            http("Delete Second Video Game")
                    .delete("/videogames/#{secondGameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.status").is("success"))
                    .check(jsonPath("$.data").isNull())

    );

    // Define the scenario by chaining together the above steps
    private final ScenarioBuilder scn = scenario("Videogame Test-Find maximum")
            .exec(getAllVideoGames)
            .pause(1)
            .exec(loginUser)
            .pause(1)
            .exec(createOriginalGame)
            .pause(1)
            .exec(updateOriginalGame)
            .pause(1)
            .exec(createSecondGame)
            .pause(1)
            .exec(deleteSecondGame);

    {
        // Set up load profile:
        // Inject 5 new users every 1 minute until total 25 users are reached
        setUp(
                scn.injectOpen(
                        rampUsers(25).during(Duration.ofMinutes(5))
                )
        ).protocols(httpProtocol);
    }
}
