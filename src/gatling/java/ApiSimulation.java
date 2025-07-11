import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

    public class ApiSimulation extends Simulation {
        //Set up http protocol
        HttpProtocolBuilder httpProtocol = http
                .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
                .acceptHeader("application/json")
                .contentTypeHeader("application/json");

        private static final String LOGIN_BODY = """
            {
            "username": "anna",
            "password": "test00"
            }
            """;

        private static final String CREATE_GAME_BODY = """
            {
                "title": "Anna Game",
                "developer": "Anna Developer",
                "publisher": "Anna Publisher",
                "releaseYear": 2024,
                "genre": "Action",
                "platform": "PC",
                "price": 100.00
            }
            """;

        private static final String UPDATE_GAME_BODY = """
            {
                "title": "Anna Game Update",
                "developer": "Anna Developer Update",
                "publisher": "Anna Publisher Update",
                "releaseYear": 2024,
                "genre": "Action",
                "platform": "PC",
                "price": 100.00
            }
            """;

        private static final String SECOND_GAME_BODY = """
            {
                "title": "Anna Game 2",
                "developer": "Anna Developer 2",
                "publisher": "Anna Publisher 2",
                "releaseYear": 2024,
                "genre": "Action",
                "platform": "PC",
                "price": 100.00
            }
            """;



        // get all vidogames1.
        private final ChainBuilder getAllVideoGames = exec(
                http("Get All VideoGames ")
                        .get("/videogames")
                        .check(status().is(200))
        );
        //2. login user
        private final ChainBuilder loginUser = exec(
                http("Login User")
                        .post("/auth/login")
                        .body(StringBody(LOGIN_BODY))
                        .check(status().is(200))
                        .check(jsonPath("$.data").saveAs("authToken"))
        );

        private final ChainBuilder createVideoGame = exec(
                http("Create Video Game")
                        .post("/videogames")
                        .header("Authorization", "Bearer #{authToken}")
                        .body(StringBody(CREATE_GAME_BODY))
                        .check(status().is(201))
                        .check(jsonPath("$.data.id").saveAs("firstGameId"))
        );

        private final ChainBuilder createsecondVideoGame = exec(
                http("Create Video Game")
                        .post("/videogames")
                        .header("Authorization", "Bearer #{authToken}")
                        .body(StringBody(SECOND_GAME_BODY))
                        .check(status().is(201))
                        .check(jsonPath("$.data.id").saveAs("secondGameId"))
        );

        private final ChainBuilder getVideoGame = exec(
                http("Get Video Game")
                        .get("/videogames/#{firstGameId}")
                        .header("Authorization", "Bearer #{authToken}")
                        .check(status().is(200))
                        .check(jsonPath("$.data.title").is("Anna Game"))
        );

        private final ChainBuilder updateVideoGame = exec(
                http("Update Video Game")
                        .put("/videogames/#{firstGameId}")
                        .header("Authorization", "Bearer #{authToken}")
                        .body(StringBody(UPDATE_GAME_BODY))
                        .check(status().is(200))
                        .check(
                                jsonPath("$.data.title").is("Anna Game Update"),
                                jsonPath("$.data.developer").is("Anna Developer Update"),
                                jsonPath("$.data.publisher").is("Anna Publisher Update")
                        )
        );

        private final ChainBuilder deleteVideoGame = exec(
                http("Delete Video Game")
                        .delete("/videogames/#{secondGameId}")
                        .header("Authorization", "Bearer #{authToken}")
                        .check(status().is(200))
        );


        // Scenario definition
        ScenarioBuilder scn = scenario("Find maximum")
                .pace(10).during(300).on(
                        pause(1)
                                .exec(loginUser)
                                .pause(1)
                                .exec(createVideoGame)
                                .pause(1)
                                .exec(updateVideoGame)
                                .pause(1)
                                .exec(createsecondVideoGame)
                                .pause(1)
                                .exec(deleteVideoGame));

        // GET request example

        {
            // Set up the simulation with the scenario
            setUp(
                    scn.injectOpen(rampUsers(5).during(1),
                            nothingFor(59),
                            rampUsers(5).during(1),
                            nothingFor(59),
                            rampUsers(5).during(1),
                            nothingFor(59),
                            rampUsers(5).during(1),
                            nothingFor(59),
                            rampUsers(5).during(1)
                    )
            ).protocols(httpProtocol);
        }

    }
