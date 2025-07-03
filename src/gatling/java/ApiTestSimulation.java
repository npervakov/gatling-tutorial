import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class ApiTestSimulation extends Simulation {

    private static final String LOGIN_BODY = "{\n" +
            "    \"username\": \"ekaterina\",\n" +
            "    \"password\": \"ekaterina\"\n" +
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
        "    \"title\": \"StepByStep\",\n" +
        "    \"developer\": \"Anna Craft\",\n" +
        "    \"publisher\": \"Tom Mennen\",\n" +
        "    \"releaseYear\": 2025,\n" +
        "    \"genre\": \"Action\",\n" +
        "    \"platform\": \"Mobile\",\n" +
        "    \"price\": 9.99\n" +
        "}";

private static final String UPDATE_GAME_BODY = "{\n" +
        "    \"title\": \"StepByStep\",\n" +
        "    \"developer\": \"Anna Craft\",\n" +
        "    \"publisher\": \"Rain Yen\",\n" +
        "    \"releaseYear\": 2025,\n" +
        "    \"genre\": \"Action\",\n" +
        "    \"platform\": \"Mobile, PC\",\n" +
        "    \"price\": 13.00\n" +
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
                    .check(jsonPath("$.data.title").is("StepByStep"))
    );

    private final ChainBuilder updateVidioGame = exec(
            http("Update Video Game")
                    .put("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(UPDATE_GAME_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("StepByStep"))
    );

    private final ChainBuilder deleteVideoGame = exec(
            http("Get Video Game")
                    .get("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("StepByStep"))
    );


    private final ScenarioBuilder scn = scenario("Videogame Test")
            .pace(12).during(300).on(
            pause(1)
            .exec(getAllVideoGames)
            .pause(1)
            .exec(loginUser)
            .pause(1)
            .exec(createVideoGame)
            .pause(1)
            .exec(getVideoGame)
            .pause(1)
            .exec(updateVidioGame)
            .pause(1)
            .exec(deleteVideoGame)
        );

    {
        // Set up the simulation with the scenario
        setUp(
                scn.injectOpen(
                        rampUsers(25).during(300)
                )

        ).protocols(httpProtocol);
    }

}
