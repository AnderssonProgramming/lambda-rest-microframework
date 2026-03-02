package co.edu.escuelaing.microframework;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the routing table for REST service endpoints.
 * 
 * <p>Routes are stored as a mapping from HTTP method + path to their
 * corresponding {@link RestHandler} lambda functions.</p>
 * 
 * <p>This class is the backbone of the microframework's REST routing system,
 * enabling developers to register and look up route handlers efficiently.</p>
 * 
 * @author Andersson David Sanchez Mendez
 * @version 1.0
 */
public class RouteHandler {

    private final Map<String, RestHandler> routes;

    /**
     * Constructs a new RouteHandler with an empty routing table.
     */
    public RouteHandler() {
        this.routes = new HashMap<>();
    }

    /**
     * Registers a GET route with the specified path and handler.
     *
     * @param path    the URL path pattern (e.g., "/hello")
     * @param handler the lambda handler to execute for this route
     */
    public void addGetRoute(String path, RestHandler handler) {
        String normalizedPath = normalizePath(path);
        routes.put("GET:" + normalizedPath, handler);
    }

    /**
     * Looks up a handler for the given HTTP method and path.
     *
     * @param method the HTTP method (e.g., "GET")
     * @param path   the URL path to match
     * @return the matching RestHandler, or null if no route matches
     */
    public RestHandler findHandler(String method, String path) {
        String normalizedPath = normalizePath(path);
        return routes.get(method.toUpperCase() + ":" + normalizedPath);
    }

    /**
     * Checks if a route exists for the given method and path.
     *
     * @param method the HTTP method
     * @param path   the URL path
     * @return true if a handler is registered for this route
     */
    public boolean hasRoute(String method, String path) {
        return findHandler(method, path) != null;
    }

    /**
     * Returns the number of registered routes.
     *
     * @return the route count
     */
    public int getRouteCount() {
        return routes.size();
    }

    /**
     * Returns all registered route keys (for debugging/logging).
     *
     * @return a map of route keys to their handlers
     */
    public Map<String, RestHandler> getRoutes() {
        return new HashMap<>(routes);
    }

    /**
     * Normalizes a path to ensure consistent matching.
     * Ensures the path starts with '/' and removes trailing slashes.
     *
     * @param path the path to normalize
     * @return the normalized path
     */
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        // Remove trailing slash (except for root)
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
