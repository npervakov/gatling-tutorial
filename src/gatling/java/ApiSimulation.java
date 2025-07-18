import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.Arrays;
import java.util.ResourceBundle;

// Websites
// http://qa-testing.in.devexperts.com:7641/    //Create login users
// http://qa-testing.in.devexperts.com:7641/swagger-ui/index.html#/Video%20Games/getAllVideoGames  // API


public class ApiSimulation extends Simulation {
    //Set up http protocol
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://qa-testing.in.devexperts.com:7641/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final String LOGIN_BODY = """
            {
            "username": "cmmoreiragit",
            "password": "cmmoreiragit"
            }
            """;

    // Create Game A
    private static final String CREATE_GAME_BODY_A = """
        {
          "title": "CFM Test Gamewww",
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
          "title": "CFM Test Gamewww v2",
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
          "title": "CFM Test Gamewww Special",
          "developer": "Test Developer",
          "publisher": "Test Publisher",
          "releaseYear": 2090,
          "genre": "Adventure",
          "platform": "PlayStation 5 Specialwww",
          "price": 99.99
        }
        """;

    // get all videogames
    private final ChainBuilder getAllVideoGames = exec(
            http("=====Get All VideoGames=====")
                    .get("/videogames")
                    .check(status().is(200))
    );
    // login
    private final ChainBuilder loginUser = exec(
            http("=====Login User=====")
                    .post("/auth/login")
                    .body(StringBody(LOGIN_BODY))
                    .check(status().is(200))
                    .check(jsonPath("$.data").saveAs("authToken"))
    );

    // Create Game A
    private final ChainBuilder createGameA = exec(
            http("=====Create Game A=====")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY_A))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameIdA"))
    );

    // Create Game B
    private final ChainBuilder createGameB = exec(
            http("=====Create Game B=====")
                    .post("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(CREATE_GAME_BODY_B))
                    .check(status().is(201))
                    .check(jsonPath("$.data.id").saveAs("gameIdB"))
    );

    //Get ID Game A
    private final ChainBuilder getVideoGame = exec(
            http("=====Get Video Game=====")
                    .get("/videogames/#{gameIdA}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(jsonPath("$.data.title").is("CFM Test Gamewww"))
    );

    // Update Game A
    private final ChainBuilder updateGameA = exec(
            http("=====Update Game A=====")
                    .put("/videogames/#{gameIdA}")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(StringBody(UPDATE_GAME_BODY))
                    .check(status().is(200))
                    //.check(jsonPath("$.data.title").is("CFM Test Gamezzz v.2"))
                    //.check(jsonPath("$.data.genre").is("Adventure"))
    );

    // Log ID Game B
    private final ChainBuilder logGameIdA = exec(session -> {
        String gameId = session.getString("gameIdB");
        System.out.println("******Deleting gameIdB = " + gameId);
        return session;
    });

    // Delete Game B
    private final ChainBuilder deleteGameB = exec(
            http("******Delete Game B******")
                    .delete("/videogames/#{gameIdB}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
    );

    //Find all Games after ID_X
    // (Chat GPT solution - to solve error on website API
    // "Could not render responses_Responses, see the console." and for troubleshoot)
    private final ChainBuilder getGamesAndFilterById = exec(
            http(">>>>>>Get All Games<<<<<<<")
                    .get("/videogames")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                    .check(bodyString().saveAs("allGamesJson"))
    ).exec(session -> {
        // Obter JSON como String
        String json = session.getString("allGamesJson");

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(json);
            int maxId = -1;

            if (root.has("data") && root.get("data").isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode game : root.get("data")) {
                    int id = game.get("id").asInt();
                    if (id > maxId) {
                        maxId = id; // Find last ID
                    }
                }

                int dynamicThreshold = maxId - 20; // +++++ New limit dinamic
                System.out.println("+++++ Last ID created: " + maxId);
                System.out.println("+++++ Games with ID > " + dynamicThreshold);

                // +++++ Browse the games again, now filtering based on the new limit
                for (com.fasterxml.jackson.databind.JsonNode game : root.get("data")) {
                    int id = game.get("id").asInt();
                    if (id > dynamicThreshold) {
                        String title = game.get("title").asText();
                        System.out.println("• ID: " + id + " | Title: " + title);

                        // Show details
                        System.out.println("+++++ Details of the last games:");
                        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(game));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(">>>>>Error reading JSON: " + e.getMessage());
        }

        return session;
    });


    // Scenario
    ScenarioBuilder scn = scenario(">>>>Videogame Flow: Update, Add, Delete<<<<")
            .exec(loginUser)
            .pause(1)
            .exec(getGamesAndFilterById)
            .pause(60)
            .exec(createGameA)
            .pause(1)
            .exec(updateGameA)
            .pause(1)
            .exec(createGameB)
            .pause(1)
            .exec(getGamesAndFilterById)
            .pause(60)
            .exec(logGameIdA)
            .exec(deleteGameB)
            .exec(getGamesAndFilterById)
            .pause(60);

    // Additional scenario: find maximum
    ScenarioBuilder loadScenario = scenario("////Find Maximum////")
            .exec(loginUser)
            .pause(1);
    {
        setUp(
                scn.injectOpen(atOnceUsers(1)),
                loadScenario.injectOpen(rampUsers(25).during(300)) // 5 users/minutes until 25
        ).protocols(httpProtocol);
    }
}