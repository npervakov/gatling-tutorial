import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

public class ApiSimulation extends Simulation {
    //Setup user data
    private static final String LOGIN_BODY = "{\n" +
            "    \"username\": \"gchambe\",\n" +
            "    \"password\": \"gchambe\"\n" +
            "}";

    //Setup Game A data
    private static final String CREATE_GAME_BODY_A = """
            {
                "title": "SuperMarioBros1985",
                "developer": "Nintendo R&D4",
                "publisher": "Nintendo",
                "releaseYear": 1985,
                "genre": "Platformer",
                "platform": "Nintendo Entertrainment System",
                "price": 19.99
            }
            """;

    //Setup Game A latest Data
    private static final String UPDATE_GAME_BODY_A = """
            {
                "title": "SuperMarioBros2025",
                "developer": "Nintendo R&D4",
                "publisher": "Nintendo",
                "releaseYear": 2025,
                "genre": "Platformer",
                "platform": "Nintendo Entertrainment System",
                "price": 79.99
            }
            """;

    //Setup Game B data
    private static final String CREATE_GAME_BODY_B = """
            {
                "title": "SuperMario64",
                "developer": "Nintendo EAD",
                "publisher": "Nintendo",
                "releaseYear": 1996,
                "genre": "3D Platformer",
                "platform": "Nintendo 64",
                "price": 59.99
            }
            """;

    //Set up http protocol
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    //1. Get All Video Games
    private final ChainBuilder getAllVideoGames = exec(
            http("Get All VideoGames ")
                    .get("/videogames")
                    .check(status().is(200))
    );

    //2. Login User
    private final ChainBuilder loginUser = exec(
            http("Login User")
                    .post("/auth/login")
                    .body(StringBody(LOGIN_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data").saveAs("authToken"))
    );

    //Create Video Game A
    private final ChainBuilder createVideoGameA = exec(
            http("Create Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY_A))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameIdA"))
    );

    //Get video game by id
    private final ChainBuilder getVideoGame = exec(
            http("Get Video Game")
                    .get("/videogames/#{gameIdA}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("SuperMarioBros1985"))
    );

    //Update Game A
    private final ChainBuilder updateVideoGameA = exec(
            http("Create Video Game")
                    .put("/videogames/#{gameIdA}")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(UPDATE_GAME_BODY_A))
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("SuperMarioBros2025"))
    );

    //Create Video Game B
    private final ChainBuilder createVideoGameB = exec(
            http("Create Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY_B))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameIdB"))
    );

    //Delete video game B
    private final ChainBuilder deleteVideoGameB = exec(
            http("Get Video Game")
                    .delete("/videogames/#{gameIdB}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
    );

    // Scenario definition
    private final ScenarioBuilder scn = scenario("Videogame Test")
            .pace(10).during(60).on(
                    pause(1)
                            .exec(getAllVideoGames)
                            .pause(1)
                            .exec(loginUser)
                            .pause(1)
                            .exec(createVideoGameA)
                            .pause(1)
                            .exec(getVideoGame)
                            .pause(1)
                            .exec(updateVideoGameA)
                            .pause(1)
                            .exec(createVideoGameB)
                            .pause(1)
                            .exec(deleteVideoGameB)
            );

    private final ScenarioBuilder scn_find_maximum = scenario("Videogame Test - Home Task")
            .pause(1)
            .exec(getAllVideoGames)
            .pause(1)
            .exec(loginUser)
            .pause(1)
            .exec(createVideoGameA)
            .pause(1)
            .exec(getVideoGame)
            .pause(1)
            .exec(updateVideoGameA)
            .pause(1)
            .exec(createVideoGameB)
            .pause(1)
            .exec(deleteVideoGameB);


    {
        // Set up the simulation with the scenario
        setUp(
                scn.injectOpen(rampUsers(10).during(20)),
                scn_find_maximum.injectOpen(
                        atOnceUsers(5),
                        nothingFor(Duration.ofMinutes(1)),
                        atOnceUsers(5),
                        nothingFor(Duration.ofMinutes(1)),
                        atOnceUsers(5),
                        nothingFor(Duration.ofMinutes(1)),
                        atOnceUsers(5),
                        nothingFor(Duration.ofMinutes(1)),
                        atOnceUsers(5)
                )

        ).protocols(httpProtocol);
    }


}
