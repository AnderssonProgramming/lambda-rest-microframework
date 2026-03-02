package co.edu.escuelaing.microframework;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lightweight HTTP server and web microframework that supports REST services
 * via lambda functions and static file serving.
 * 
 * <p>This is the main entry point of the framework. It provides static methods
 * that allow developers to define routes and configure the server with minimal
 * boilerplate code.</p>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Define REST GET services using lambda functions via {@link #get(String, RestHandler)}</li>
 *   <li>Extract query parameters from requests via {@link Request#getValues(String)}</li>
 *   <li>Configure static file location via {@link #staticfiles(String)}</li>
 *   <li>Automatic MIME type detection for static files</li>
 *   <li>Handles multiple sequential (non-concurrent) HTTP requests</li>
 * </ul>
 * 
 * <h2>Example Usage:</h2>
 * <pre>{@code
 * public static void main(String[] args) {
 *     staticfiles("/webroot");
 *     get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
 *     get("/pi", (req, resp) -> String.valueOf(Math.PI));
 * }
 * }</pre>
 * 
 * @author Andersson David Sanchez Mendez
 * @version 1.0
 */
public class MicroServer {

    private static final Logger LOGGER = Logger.getLogger(MicroServer.class.getName());
    private static final int DEFAULT_PORT = 8080;
    private static final String REST_PREFIX = "/App";

    private static MicroServer instance;
    private final RouteHandler routeHandler;
    private StaticFileHandler staticFileHandler;
    private int port;
    private boolean running;

    /**
     * Private constructor (Singleton pattern).
     * Initializes the server with default configuration.
     */
    private MicroServer() {
        this.routeHandler = new RouteHandler();
        this.staticFileHandler = new StaticFileHandler("/webroot");
        this.port = DEFAULT_PORT;
        this.running = false;
    }

    /**
     * Returns the singleton instance of MicroServer, creating it if needed.
     *
     * @return the MicroServer instance
     */
    public static synchronized MicroServer getInstance() {
        if (instance == null) {
            instance = new MicroServer();
        }
        return instance;
    }

    /**
     * Registers a GET REST service route with a lambda handler.
     * 
     * <p>Routes are prefixed with "/App" by default. For example,
     * registering "/hello" makes the service available at "/App/hello".</p>
     *
     * @param path    the route path (e.g., "/hello")
     * @param handler the lambda handler (req, resp) -> String
     */
    public static void get(String path, RestHandler handler) {
        getInstance().routeHandler.addGetRoute(path, handler);
        LOGGER.log(Level.INFO, "Registered GET route: {0}{1}", new Object[]{REST_PREFIX, path});
    }

    /**
     * Sets the directory where static files are located.
     * 
     * <p>The directory is relative to the classpath. For example,
     * setting "/webroot" will serve files from "target/classes/webroot/".</p>
     *
     * @param folder the static files directory path
     */
    public static void staticfiles(String folder) {
        getInstance().staticFileHandler = new StaticFileHandler(folder);
        LOGGER.log(Level.INFO, "Static files directory set to: {0}", folder);
    }

    /**
     * Starts the HTTP server on the default port (8080).
     * The server listens for incoming connections and handles them sequentially.
     */
    public static void start() {
        start(DEFAULT_PORT);
    }

    /**
     * Starts the HTTP server on the specified port.
     *
     * @param port the port number to listen on
     */
    public static void start(int port) {
        MicroServer server = getInstance();
        server.port = port;
        server.running = true;
        server.listen();
    }

    /**
     * Main server loop. Accepts connections and dispatches requests.
     */
    private void listen() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.log(Level.INFO, "==============================================");
            LOGGER.log(Level.INFO, " Lambda REST Microframework");
            LOGGER.log(Level.INFO, " Server started on port {0}", String.valueOf(port));
            LOGGER.log(Level.INFO, " http://localhost:{0}/", String.valueOf(port));
            LOGGER.log(Level.INFO, " REST prefix: {0}", REST_PREFIX);
            LOGGER.log(Level.INFO, " Static files: {0}", staticFileHandler.getStaticFolder());
            LOGGER.log(Level.INFO, " Routes registered: {0}", String.valueOf(routeHandler.getRouteCount()));
            LOGGER.log(Level.INFO, "==============================================");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error accepting connection: {0}", e.getMessage());
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not listen on port {0}: {1}",
                    new Object[]{String.valueOf(port), e.getMessage()});
        }
    }

    /**
     * Handles a single HTTP client connection.
     * Parses the request, routes it to the appropriate handler, and sends the response.
     *
     * @param clientSocket the client socket connection
     */
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            // Parse HTTP request line
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }

            LOGGER.log(Level.INFO, "Request: {0}", requestLine);

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 3) {
                sendErrorResponse(out, 400, "Bad Request");
                clientSocket.close();
                return;
            }

            String method = requestParts[0];
            String fullUri = requestParts[1];

            // Parse headers
            Map<String, String> headers = parseHeaders(in);

            // Parse URI: separate path and query string
            String path;
            String queryString = "";
            if (fullUri.contains("?")) {
                int queryIndex = fullUri.indexOf('?');
                path = fullUri.substring(0, queryIndex);
                queryString = fullUri.substring(queryIndex + 1);
            } else {
                path = fullUri;
            }

            // Parse query parameters
            Map<String, String> queryParams = Request.parseQueryString(queryString);

            // Create Request and Response objects
            Request request = new Request(method, path, queryParams, headers, "");
            Response response = new Response();

            // Route the request
            if (path.startsWith(REST_PREFIX + "/") || path.equals(REST_PREFIX)) {
                // REST API request - strip the prefix
                String restPath = path.substring(REST_PREFIX.length());
                if (restPath.isEmpty()) restPath = "/";
                handleRestRequest(method, restPath, request, response, out);
            } else {
                // Static file request
                handleStaticFileRequest(path, out);
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error handling client: {0}", e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error closing socket: {0}", e.getMessage());
            }
        }
    }

    /**
     * Handles a REST API request by looking up and invoking the registered handler.
     *
     * @param method   the HTTP method
     * @param restPath the REST path (without prefix)
     * @param request  the parsed Request object
     * @param response the Response object
     * @param out      the output stream to write the response
     * @throws IOException if an I/O error occurs
     */
    private void handleRestRequest(String method, String restPath, Request request,
                                   Response response, OutputStream out) throws IOException {
        RestHandler handler = routeHandler.findHandler(method, restPath);

        if (handler != null) {
            try {
                String body = handler.handle(request, response);
                response.setBody(body);
                sendHttpResponse(out, response.getStatusCode(), "text/html", body.getBytes());
                LOGGER.log(Level.INFO, "REST {0} {1} -> 200 OK", new Object[]{method, restPath});
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error in handler for {0}: {1}",
                        new Object[]{restPath, e.getMessage()});
                sendErrorResponse(out, 500, "Internal Server Error: " + e.getMessage());
            }
        } else {
            LOGGER.log(Level.WARNING, "No handler found for {0} {1}", new Object[]{method, restPath});
            sendErrorResponse(out, 404, "REST endpoint not found: " + restPath);
        }
    }

    /**
     * Handles a static file request by looking up the file and sending it.
     *
     * @param path the file path from the request
     * @param out  the output stream to write the response
     * @throws IOException if an I/O error occurs
     */
    private void handleStaticFileRequest(String path, OutputStream out) throws IOException {
        // Default to index.html for root
        if (path.equals("/")) {
            path = "/index.html";
        }

        byte[] fileBytes = staticFileHandler.getFileBytes(path);
        if (fileBytes != null) {
            String contentType = StaticFileHandler.getContentType(path);
            sendHttpResponse(out, 200, contentType, fileBytes);
            LOGGER.log(Level.INFO, "Static file served: {0} ({1})", new Object[]{path, contentType});
        } else {
            LOGGER.log(Level.WARNING, "Static file not found: {0}", path);
            sendErrorResponse(out, 404, "File not found: " + path);
        }
    }

    /**
     * Sends an HTTP response with the given status, content type, and body.
     *
     * @param out         the output stream
     * @param statusCode  the HTTP status code
     * @param contentType the MIME content type
     * @param body        the response body as bytes
     * @throws IOException if an I/O error occurs
     */
    private void sendHttpResponse(OutputStream out, int statusCode, String contentType,
                                  byte[] body) throws IOException {
        String statusMessage = new Response().setStatusCode(statusCode).getStatusMessage();
        String header = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.write(header.getBytes());
        out.write(body);
        out.flush();
    }

    /**
     * Sends an HTML error response with the given status code and message.
     *
     * @param out        the output stream
     * @param statusCode the HTTP status code
     * @param message    the error message
     * @throws IOException if an I/O error occurs
     */
    private void sendErrorResponse(OutputStream out, int statusCode, String message) throws IOException {
        String body = "<!DOCTYPE html>\n<html><head><title>" + statusCode + " Error</title>\n" +
                "<style>body{font-family:Arial,sans-serif;text-align:center;padding:50px;}" +
                "h1{color:#e74c3c;}p{color:#555;}</style></head>\n" +
                "<body><h1>" + statusCode + "</h1><p>" + message + "</p>" +
                "<hr><p><em>Lambda REST Microframework</em></p></body></html>";
        sendHttpResponse(out, statusCode, "text/html", body.getBytes());
    }

    /**
     * Parses HTTP headers from the input reader.
     *
     * @param in the buffered reader for the client input
     * @return a map of header names to values
     * @throws IOException if an I/O error occurs
     */
    private Map<String, String> parseHeaders(BufferedReader in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String name = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(name, value);
            }
        }
        return headers;
    }

    /**
     * Returns the configured REST prefix path.
     *
     * @return the REST prefix (e.g., "/App")
     */
    public static String getRestPrefix() {
        return REST_PREFIX;
    }

    /**
     * Returns the RouteHandler (primarily for testing).
     *
     * @return the route handler
     */
    public RouteHandler getRouteHandler() {
        return routeHandler;
    }

    /**
     * Returns the StaticFileHandler (primarily for testing).
     *
     * @return the static file handler
     */
    public StaticFileHandler getStaticFileHandler() {
        return staticFileHandler;
    }

    /**
     * Stops the server gracefully.
     */
    public void stop() {
        this.running = false;
    }

    /**
     * Resets the singleton instance (primarily for testing).
     */
    public static synchronized void reset() {
        instance = null;
    }
}
