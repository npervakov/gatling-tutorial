import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;


public class HomeSimulation extends Simulation
{
    private static final String LOGIN_BODY = "{\n" +
            "    \"username\": \"test_user\",\n" +
            "    \"password\": \"Test2025\"\n" +
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


    //Previous game body update
    private static final String NEW_GAME_BODY = "{\n" +
            "    \"title\": \"Previous Game\",\n" +
            "    \"developer\": \"Test Developer\",\n" +
            "    \"publisher\": \"Test Publisher\",\n" +
            "    \"releaseYear\": 2024,\n" +
            "    \"genre\": \"Action\",\n" +
            "    \"platform\": \"PC\",\n" +
            "    \"price\": 300\n" +
            "}";

    //Update previous game
    private final ChainBuilder updateVideoGame = exec(
            http("Update Video Game")
                    .put("/videogames/8")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(NEW_GAME_BODY))
                    .check(status().is(200))
    );

    //New game
    private static final String CREATE_GAME_BODY = "{\n" +
                " \"title\": \"Amazing Game\",\n" +
                " \"developer\": \"Banksy Developer\",\n" +
                " \"publisher\": \"Censorship Publisher\",\n" +
                " \"releaseYear\": 2025,\n" +
                " \"genre\": \"Action\",\n" +
                " \"platform\": \"PC\", \n" +
                " \"price\": 100\n" +
            "}";

    private final ChainBuilder createVideoGame = exec(
            http("Create Video Game")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameId"))
    );

    //Delete previous game
    private final ChainBuilder deleteVideoGame = exec(
            http("Delete Video Game")
                    .delete("/videogames/8")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))

    );

    private final ChainBuilder getVideoGame = exec(
            http("Get Video Game")
                    .get("/videogames/#{gameId}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("Amazing Game"))
    );

    private final ScenarioBuilder scn = scenario("Update, create, delete and test 5 users/min; 25 max")
            .pause(1)
            .exec(getAllVideoGames)
            .pause(1)
            .exec(loginUser)
            .pause(1)
            .exec(updateVideoGame)
            .pause(1)
            .exec(createVideoGame)
            .pause(1)
            .exec(deleteVideoGame)
            .pause(1)
            .exec(getVideoGame);

    {
        setUp(
                scn.injectOpen(rampUsers(25).during(300))
                ).protocols(httpProtocol);
    }
}
