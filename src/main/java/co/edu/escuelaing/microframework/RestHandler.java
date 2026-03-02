package co.edu.escuelaing.microframework;

/**
 * Functional interface representing a REST service handler.
 * 
 * <p>This interface uses the SAM (Single Abstract Method) pattern,
 * enabling developers to define REST service handlers using lambda expressions.</p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * RestHandler handler = (req, resp) -> "Hello " + req.getValues("name");
 * }</pre>
 * 
 * @author Andersson David Sanchez Mendez
 * @version 1.0
 */
@FunctionalInterface
public interface RestHandler {

    /**
     * Handles an incoming HTTP request and produces a response body.
     *
     * @param request  the incoming HTTP request with parsed query parameters
     * @param response the HTTP response object (for future header manipulation)
     * @return the response body as a String
     */
    String handle(Request request, Response response);
}
