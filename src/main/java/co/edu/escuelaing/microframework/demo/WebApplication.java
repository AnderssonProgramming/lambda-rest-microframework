package co.edu.escuelaing.microframework.demo;

import co.edu.escuelaing.microframework.MicroServer;

import static co.edu.escuelaing.microframework.MicroServer.get;
import static co.edu.escuelaing.microframework.MicroServer.staticfiles;
import static co.edu.escuelaing.microframework.MicroServer.start;

/**
 * Demo web application showcasing the Lambda REST Microframework.
 * 
 * <p>This example demonstrates how developers can use the framework to:
 * <ul>
 *   <li>Set the static files directory</li>
 *   <li>Define REST GET services using lambda expressions</li>
 *   <li>Access query parameters within service handlers</li>
 * </ul>
 * </p>
 * 
 * <h2>Available Endpoints:</h2>
 * <table>
 *   <tr><th>URL</th><th>Description</th></tr>
 *   <tr><td>http://localhost:8080/index.html</td><td>Static HTML page</td></tr>
 *   <tr><td>http://localhost:8080/App/hello?name=Pedro</td><td>Greeting service</td></tr>
 *   <tr><td>http://localhost:8080/App/pi</td><td>Returns PI value</td></tr>
 *   <tr><td>http://localhost:8080/App/greeting?name=World</td><td>HTML greeting</td></tr>
 * </table>
 * 
 * @author Andersson David Sanchez Mendez
 * @version 1.0
 */
public class WebApplication {

    /**
     * Main entry point for the demo web application.
     * Configures static files and REST services, then starts the server.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Configure the static files directory
        staticfiles("/webroot");

        // REST service: simple greeting with query parameter
        get("/hello", (req, resp) -> "Hello " + req.getValues("name"));

        // REST service: returns the value of PI
        get("/pi", (req, resp) -> String.valueOf(Math.PI));

        // REST service: HTML greeting page with query parameter
        get("/greeting", (req, resp) -> {
            String name = req.getValues("name");
            if (name.isEmpty()) {
                name = "World";
            }
            return "<!DOCTYPE html>\n" +
                    "<html><head><title>Greeting</title></head>\n" +
                    "<body style='font-family:Arial;text-align:center;padding:50px;'>\n" +
                    "<h1>Hello, " + name + "!</h1>\n" +
                    "<p>Welcome to the Lambda REST Microframework.</p>\n" +
                    "<p><a href='/index.html'>Back to Home</a></p>\n" +
                    "</body></html>";
        });

        // REST service: returns current server time
        get("/time", (req, resp) -> {
            return "Current server time: " + new java.util.Date().toString();
        });

        // REST service: echoes all query parameters
        get("/echo", (req, resp) -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Echo Service\n");
            sb.append("============\n");
            req.getQueryParams().forEach((key, value) ->
                    sb.append(key).append(" = ").append(value).append("\n")
            );
            if (req.getQueryParams().isEmpty()) {
                sb.append("No query parameters provided.\n");
                sb.append("Try: /App/echo?message=hello&from=user\n");
            }
            return sb.toString();
        });

        // Start the server on port 8080
        start();
    }
}
