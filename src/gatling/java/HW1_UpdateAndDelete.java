import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class HW1_UpdateAndDelete extends Simulation {

    private static final String LOGIN_BODY =
            "{\n" +
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
            "title": "Path of Exile",
            "developer": "Grinding Gear Games",
            "publisher": "Grinding Gear Games",
            "releaseYear": 2013,
            "genre": "ARP",
            "platform": "PC, macOS, Xbox One, PS4",
            "price": 0
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
                    .check(jsonPath("$.data.title").is("Path of Exile"))
    );

    private static final String UPDATE_GAME_BODY = """
            {
            "title": "Path of Exile II",
            "developer": "Grinding Gear Games",
            "publisher": "Grinding Gear Games",
            "releaseYear": 2024,
            "genre": "ARP",
            "platform": "PC, Xbox X/S, PS5",
            "price": 27.75
            }
            """;

    private final ChainBuilder updateVideoGame = exec(
            http("Update Video Game")
                    .put("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(UPDATE_GAME_BODY))
                    .check(status().is(200))
    );
    private final ChainBuilder getVideoGame2 = exec(
            http("Get Video Game 2")
                    .get("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("Path of Exile II"))
    );
    private final ChainBuilder deleteVideoGame = exec(
             http("Delete Video Game")
                    .delete("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
        );

    private final ScenarioBuilder scn = scenario("Update and Delete Video Games")
            .pause(1)
            .exec(getAllVideoGames)
            .pause(1)
            .exec(loginUser)
            .pause(1)
            .exec(createVideoGame)
            .pause(1)
            .exec(getVideoGame)
            .pause(1)
            .exec(updateVideoGame)
            .pause(1)
            .exec(getVideoGame2)
            .pause(1)
            .exec(deleteVideoGame)
            ;

    {
        // Set up the simulation with the scenario
        setUp(
                scn.injectOpen(atOnceUsers(5))
        ).protocols(httpProtocol);
    }
}