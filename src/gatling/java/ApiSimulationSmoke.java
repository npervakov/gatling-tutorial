import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class ApiSimulationSmoke extends Simulation {

    // Reuse login JSON payload
    private static final String LOGIN_BODY = "{\n" +
            "    \"username\": \"testuserBatman\",\n" +
            "    \"password\": \"helloworld2\"\n" +
            "}";

    // Set up http protocol
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // 1. Get all videogames
    private final ChainBuilder getAllVideoGames = exec(
            http("Get All VideoGames ")
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

    // JSON payload for creating the new game
    private static final String CREATE_GAME_BODY = """
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

    // 3. Create a new game
    private final ChainBuilder createVideoGame = exec(
            http("Create Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameId"))
    );

    // 4. Get the created game by its ID
    private final ChainBuilder getVideoGame = exec(
            http("Get Video Game")
                    .get("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("Battlefield"))
    );

    // Define the scenario by chaining together the above steps
    private final ScenarioBuilder scn = scenario("Videogame Test")
            .pause(1)
            .exec(getAllVideoGames)
            .pause(1)
            .exec(loginUser)
            .pause(1)
            .exec(createVideoGame)
            .pause(1)
            .exec(getVideoGame);

    {
        // Set up the simulation with the scenario:
        // Run 5 users at once
        setUp(
                scn.injectOpen(atOnceUsers(5))
        ).protocols(httpProtocol);
    }
}