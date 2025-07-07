//Test for 5 users at once

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class ApiSimulation extends Simulation
{

    //User's data
    private static final String LOGIN_BODY = "{\n" +
            "    \"username\": \"test_user\",\n" +
            "    \"password\": \"Test2025\"\n" +
            "}";

    //Set up http protocol
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
            .acceptHeader("application/json")
     .contentTypeHeader("application/json");

    //Get all video games
    private final ChainBuilder getAllVideoGames = exec(
            http("Get All VideoGames ")
                    .get("/videogames")
                    .check(status().is(200))
    );

    //Login
    private final ChainBuilder loginUser = exec(
            http("Login User")
                    .post("/auth/login")
                    .body(StringBody(LOGIN_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data").saveAs("authToken"))
    );

    //Game body (json)
    private static final String CREATE_GAME_BODY = """
            {
                "title": "Test Game",
                "developer": "Test Developer",
                "publisher": "Test Publisher",
                "releaseYear": 2024,
                "genre": "Action",
                "platform": "PC",
                "price": 59.99
            }
            """;

    //Create the game
    private final ChainBuilder createVideoGame = exec(
            http("Create Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameId"))
    );

    //Get the game
    private final ChainBuilder getVideoGame = exec(
            http("Get Video Game")
                    .get("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("Test Game"))
    );

    //Scenario definition
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
        //Set up the simulation with the scenario
        setUp(
                scn.injectOpen(atOnceUsers(5))
        ).protocols(httpProtocol);
    }

}

/*
//Test for 10 users at once during 20sec

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

        public class ApiSimulation extends Simulation {

            private static final String LOGIN_BODY = "{\n" +
                    "    \"username\": \"player1\",\n" +
                    "    \"password\": \"testplay\"\n" +
                    "}";
            //Set up http protocol
            HttpProtocolBuilder httpProtocol = http
                    .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
                    .acceptHeader("application/json")
                    .contentTypeHeader("application/json");

            //1.

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
            private static final String CREATE_GAME_BODY = "{\n" +
                    "    \"title\": \"Test Game\",\n" +
                    "    \"developer\": \"Test Developer\",\n" +
                    "    \"publisher\": \"Test Publisher\",\n" +
                    "    \"releaseYear\": 2024,\n" +
                    "    \"genre\": \"Action\",\n" +
                    "    \"platform\": \"PC\",\n" +
                    "    \"price\": 59.99\n" +
                    "}";

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
                            .check(jsonPath("$.data.title").is("Test Game"))
            );
            private final ScenarioBuilder scn = scenario("Videogame Test")
                    .pace(10).during(60).on(
                            pause(1)
                                    .exec(getAllVideoGames)
                                    .pause(1)
                                    .exec(loginUser)
                                    .pause(1)
                                    .exec(createVideoGame)
                                    .pause(1)
                                    .exec(getVideoGame)
                    );

            {
                // Set up the simulation with the scenario
                setUp(
                        scn.injectOpen(rampUsers(10).during(20))
                ).protocols(httpProtocol);
            }

        }

 */


