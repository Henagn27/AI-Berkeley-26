import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;
import com.openai.models.responses.ResponseOutputMessage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class main {
    private static final String OPENAI_MODEL = "gpt-5.5";

    public static void main(String[] args) {
        try {
            startServer();
        } catch (BindException error) {
            System.out.println("Could not start server because port 8000 is already in use.");
            System.out.println("That usually means the Java server is already running.");
            System.out.println("Open http://localhost:8000 in your browser, or stop the old server with:");
            System.out.println("netstat -ano | findstr :8000");
            System.out.println("taskkill /PID THE_LISTENING_PID /F");
        } catch (IOException error) {
            System.out.println("Could not start server.");
            System.out.println("Make sure this command is running inside the project folder:");
            System.out.println("C:\\Users\\dusty\\OneDrive\\Documents\\Hackathons\\AI Berkeley 26");
            System.out.println(error.getMessage());
        }
    }

    private static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // These routes let Java serve the page, the React code, and backend decisions.
        server.createContext("/", main::serveHtml);
        server.createContext("/app.js", main::serveReactApp);
        server.createContext("/decide", main::handleDecision);

        server.start();
        System.out.println("Server running at http://localhost:8000");
        System.out.println("Press Ctrl+C in this PowerShell window to stop the server.");
    }

    private static void serveHtml(HttpExchange exchange) throws IOException {
        byte[] page = Files.readAllBytes(Path.of("webpage.html"));
        sendResponse(exchange, 200, "text/html", page);
    }

    private static void serveReactApp(HttpExchange exchange) throws IOException {
        byte[] script = Files.readAllBytes(Path.of("app.js"));
        sendResponse(exchange, 200, "application/javascript", script);
    }

    private static void handleDecision(HttpExchange exchange) throws IOException {
        // Browsers may send OPTIONS before POST when making local API calls.
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            sendResponse(exchange, 204, "application/json", new byte[0]);
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendJson(exchange, 405, "{\"message\":\"Use POST for this endpoint.\"}");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String action = getJsonValue(requestBody, "action");

        String message = decide(action);
        sendJson(exchange, 200, "{\"message\":\"" + escapeJson(message) + "\"}");
    }

    private static String decide(String action) {
        // Completed route selections are encoded as route|mode|aircraft|departure|arrival.
        if (action.startsWith("route|")) {
            return decideRoute(action);
        }

        if ("ai-info".equalsIgnoreCase(action)) {
            return "Java received AI Info.";
        }

        if ("ai-cessna-172".equalsIgnoreCase(action)) {
            return "Java received AI Info for Cessna 172.";
        }

        if ("ai-beechcraft-bonanza".equalsIgnoreCase(action)) {
            return "Java received AI Info for Beechcraft Bonanza.";
        }

        if ("non-ai-info".equalsIgnoreCase(action)) {
            return "Java received Non-AI Info.";
        }

        return "Java received an unknown action: " + action;
    }

    private static String decideRoute(String action) {
        String[] parts = action.split("\\|");
        if (parts.length != 5) {
            return "Java received an incomplete route.";
        }

        String mode = parts[1].equals("ai") ? "AI" : "Non AI";
        String aircraft = formatAircraft(parts[2]);
        String departingAirport = parts[3];
        String arrivingAirport = parts[4];
        double distance = estimateDistanceNauticalMiles(departingAirport, arrivingAirport);

        // Only the AI flow calls OpenAI. The Non AI flow stays as a normal route summary.
        if ("ai".equals(parts[1])) {
            return getAiFlightPlanRecommendation(aircraft, departingAirport, arrivingAirport, distance);
        }

        return mode + " route selected: " + aircraft + " from " + departingAirport + " to " + arrivingAirport
                + ". Estimated distance: " + Math.round(distance) + " nautical miles.";
    }

    private static String formatAircraft(String aircraft) {
        if ("cessna-172".equalsIgnoreCase(aircraft)) {
            return "Cessna 172";
        }

        if ("beechcraft-bonanza".equalsIgnoreCase(aircraft)) {
            return "Beechcraft Bonanza";
        }

        return aircraft;
    }

    private static String getAiFlightPlanRecommendation(String aircraft, String departingAirport, String arrivingAirport, double distance) {
        if (System.getenv("OPENAI_API_KEY") == null || System.getenv("OPENAI_API_KEY").isBlank()) {
            return "OPENAI_API_KEY is missing. Set it in PowerShell, restart the server, and try the AI route again.";
        }

        // The prompt keeps the MVP focused: aircraft + route distance -> likely IFR or VFR.
        String prompt = "This is a hackathon demo, not real flight-planning advice. "
                + "Choose the more likely flight plan type for this simulated route: IFR or VFR. "
                + "Aircraft: " + aircraft + ". "
                + "Route: " + departingAirport + " to " + arrivingAirport + ". "
                + "Estimated great-circle distance: " + Math.round(distance) + " nautical miles. "
                + "Base the recommendation only on aircraft type and distance. "
                + "The first sentence must be exactly this format: Based on the information given, most likely the flight plan will be IFR. "
                + "or this format: Based on the information given, most likely the flight plan will be VFR. "
                + "After that, add one short reason. "
                + "End with: This is only a planning estimate; verify with official aviation tools, weather, regulations, aircraft performance, and pilot judgment.";

        try {
            OpenAIClient client = OpenAIOkHttpClient.fromEnv();

            ResponseCreateParams params = ResponseCreateParams.builder()
                    .input(prompt)
                    .model(OPENAI_MODEL)
                    .build();

            Response response = client.responses().create(params);
            return "Estimated distance: " + Math.round(distance) + " nautical miles.\n\n" + getOutputText(response);
        } catch (RuntimeException error) {
            return "OpenAI request failed: " + error.getMessage();
        }
    }

    private static double estimateDistanceNauticalMiles(String firstAirport, String secondAirport) {
        // Great-circle distance estimate using hardcoded airport coordinates.
        double[] first = airportCoordinates(firstAirport);
        double[] second = airportCoordinates(secondAirport);

        double firstLatitude = Math.toRadians(first[0]);
        double secondLatitude = Math.toRadians(second[0]);
        double latitudeDifference = Math.toRadians(second[0] - first[0]);
        double longitudeDifference = Math.toRadians(second[1] - first[1]);

        double a = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
                + Math.cos(firstLatitude) * Math.cos(secondLatitude)
                * Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 3440.065 * c;
    }

    private static double[] airportCoordinates(String airport) {
        if ("KLAX".equalsIgnoreCase(airport)) {
            return new double[] { 33.9416, -118.4085 };
        }

        if ("KSFO".equalsIgnoreCase(airport)) {
            return new double[] { 37.6213, -122.3790 };
        }

        if ("KDEN".equalsIgnoreCase(airport)) {
            return new double[] { 39.8561, -104.6737 };
        }

        if ("KSAN".equalsIgnoreCase(airport)) {
            return new double[] { 32.7338, -117.1933 };
        }

        return new double[] { 0.0, 0.0 };
    }

    private static String getOutputText(Response response) {
        // SDK 4.0.0 stores response text inside output message content items.
        StringBuilder outputText = new StringBuilder();

        for (ResponseOutputItem item : response.output()) {
            if (!item.isMessage()) {
                continue;
            }

            ResponseOutputMessage message = item.asMessage();

            message.content().forEach(content -> {
                if (content.isOutputText()) {
                    outputText.append(content.asOutputText().text());
                }
            });
        }

        if (outputText.length() == 0) {
            return "No text output was returned.";
        }

        return outputText.toString();
    }

    private static String getJsonValue(String json, String key) {
        String search = "\"" + key + "\":";
        int keyStart = json.indexOf(search);
        if (keyStart == -1) {
            return "";
        }

        int valueStart = json.indexOf("\"", keyStart + search.length());
        int valueEnd = json.indexOf("\"", valueStart + 1);
        if (valueStart == -1 || valueEnd == -1) {
            return "";
        }

        return json.substring(valueStart + 1, valueEnd);
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        sendResponse(exchange, statusCode, "application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, byte[] response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(statusCode, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }

    private static String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
