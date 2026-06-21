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

        server.createContext("/", main::serveHtml);
        server.createContext("/app.jsx", main::serveReactApp);
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
        byte[] script = Files.readAllBytes(Path.of("app.jsx"));
        sendResponse(exchange, 200, "application/javascript", script);
    }

    private static void handleDecision(HttpExchange exchange) throws IOException {
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
        if ("ai-info".equalsIgnoreCase(action)) {
            return "Java received AI Info.";
        }

        if ("ai-cessna-172".equalsIgnoreCase(action)) {
            return "Java received AI Info for Cessna 172.";
        }

        if ("ai-beechcraft-bonbanza".equalsIgnoreCase(action)) {
            return "Java received AI Info for Beechcraft Bonbanza.";
        }

        if ("non-ai-info".equalsIgnoreCase(action)) {
            return "Java received Non-AI Info.";
        }

        return "Java received an unknown action.";
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
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
