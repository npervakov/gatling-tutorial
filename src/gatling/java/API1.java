import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class API1 extends Simulation {

private static final String LOGIN_BODY = "{\n" +
        "   \"username\": \"mrangelov\",\n" +
        "    \"password\": \"Qw3rty\"\n" +
        "}";
    //Set up http protocol
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private final ChainBuilder getAllVideoGames = exec(
            http("Get All VideoGames ")
                    .get("/videogames")
                    .check(status().is(200))
    );
    private final ChainBuilder loginUser = exec(
            http("Login User")
                    .post("/auth/login")
                    .body(StringBody(LOGIN_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data").saveAs("authToken"))
    );
    private static final String CREATE_GAME_BODY = """
            {
            "title": "Clair Obscur: Expedition 33",
            "developer": "Sandfall Interactive",
            "publisher": "Kepler Interactive",
            "releaseYear": 2025,
            "genre": "RPG",
            "platform": "PC, Xbox X/S, PS5",
            "price": 29.99
            }
            """;

    private final ChainBuilder createVideoGame = exec(
            http("Create Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameId"))
    );
    private final ChainBuilder getVideoGame = exec(
            http("Get Video Game")
                    .get("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("Clair Obscur: Expedition 33"))
    );
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
        // Set up the simulation with the scenario
        setUp(
                scn.injectOpen(atOnceUsers(5))
        ).protocols(httpProtocol);
    }
}
