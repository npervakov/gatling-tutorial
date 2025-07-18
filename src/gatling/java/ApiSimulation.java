
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.Arrays;
import java.util.ResourceBundle;

public class ApiSimulation extends Simulation {

    //Set up http protocol
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final String LOGIN_BODY = """
            {
            "username": "vsousauser",
            "password": "vsousapassword"
            }
            """;

    // Create Game A
    private static final String CREATE_GAME_BODY_A = """
        {
          "title": "VS Test Game",
          "developer": "Test Developer",
          "publisher": "Test Publisher",
          "releaseYear": 2024,
          "genre": "Action",
          "platform": "PC",
          "price": 59.99
        }
        """;

    // Create Game B
    private static final String CREATE_GAME_BODY_B = """
        {
          "title": "VS Test Game 2",
          "developer": "Test Developer",
          "publisher": "Test Publisher",
          "releaseYear": 2024,
          "genre": "Action",
          "platform": "PC",
          "price": 59.99
        }
        """;

    // Update Game A
    private static final String UPDATE_GAME_BODY = """
        {
          "title": "VS Test Game",
          "developer": "Test Developer",
          "publisher": "Test Publisher",
          "releaseYear": 2024,
          "genre": "Adventure",
          "platform": "PlayStation 5",
          "price": 49.99
        }
        """;

    // get all vidogames
    private final ChainBuilder getAllVideoGames = exec(
            http("Get All VideoGames ")
                    .get("/videogames")
                    .check(status().is(200))
    );

    // login
    private final ChainBuilder loginUser = exec(
            http("Login User")
                    .post("/auth/login")
                    .body(StringBody(LOGIN_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data").saveAs("authToken"))
    );

    // Create Game A
    private final ChainBuilder createGameA = exec(
            http("Create Game A")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY_A))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameIdA"))
    );

    private final ChainBuilder getVideoGame = exec(
            http("Get Video Game")
                    .get("/videogames/#{gameIdA}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("VS Test Game"))
    );

    // Update Game A
    private final ChainBuilder updateGameA = exec(
            http("Update Game A")
                    .put("/videogames/#{gameIdA}")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(UPDATE_GAME_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data.genre").is("Adventure"))
    );


    // Create Game B
    private final ChainBuilder createGameB = exec(
            http("Create Game B")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY_B))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameIdB")) //
                    .check(bodyString().saveAs("createGameBResponse"))
    ).exec(session -> {
        System.out.println("Game B creation response: " + session.getString("createGameBResponse"));
        System.out.println("Saved gameIdB: " + session.getString("gameIdB")); // debug
        return session;
    });

    // Log ID Game B
    private final ChainBuilder logGameIdB = exec(session -> {
        String gameId = session.getString("gameIdB");
        System.out.println("Deleting gameIdB = " + gameId);
        return session;
    });

    // Delete Game B
    private final ChainBuilder deleteGameB = exec(
            http("Delete Game B")
                    .delete("/videogames/#{gameIdB}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().in(Arrays.asList(200, 202, 204)))
    );

    // Scenario
    ScenarioBuilder scn = scenario("Videogame Flow: Update, Add, Delete")
            .exec(loginUser)
            .pause(1)
            .exec(createGameA)
            .pause(1)
            .exec(updateGameA)
            .pause(1)
            .exec(createGameB)
            .pause(1)
            .exec(logGameIdB)     // log
            .exec(deleteGameB);

    {
        setUp(
                scn.injectOpen(
                        incrementUsersPerSec(5)
                                .times(5)
                                .eachLevelLasting(60)
                                .startingFrom(0)
                )
        ).protocols(httpProtocol);
    }
}
