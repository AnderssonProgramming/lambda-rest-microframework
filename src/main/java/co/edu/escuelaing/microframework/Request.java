package co.edu.escuelaing.microframework;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP request with parsed URI, query parameters, and headers.
 * 
 * <p>This class provides a clean API for REST service handlers to access
 * request data, particularly query parameters via {@link #getValues(String)}.</p>
 * 
 * <p>Example usage within a handler:</p>
 * <pre>{@code
 * get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
 * // Request: /App/hello?name=Pedro → "Hello Pedro"
 * }</pre>
 * 
 * @author Andersson David Sanchez Mendez
 * @version 1.0
 */
public class Request {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final String body;

    /**
     * Constructs a new Request with the given HTTP method, path, query parameters,
     * headers, and body.
     *
     * @param method      the HTTP method (GET, POST, etc.)
     * @param path        the request path (without query string)
     * @param queryParams the parsed query parameters
     * @param headers     the HTTP headers
     * @param body        the request body (may be empty)
     */
    public Request(String method, String path, Map<String, String> queryParams,
                   Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams != null ? new HashMap<>(queryParams) : new HashMap<>();
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
        this.body = body != null ? body : "";
    }

    /**
     * Returns the value of the specified query parameter.
     * 
     * <p>This is the primary method for extracting query values from
     * incoming requests within REST service handlers.</p>
     *
     * @param key the query parameter name
     * @return the parameter value, or an empty string if not present
     */
    public String getValues(String key) {
        return queryParams.getOrDefault(key, "");
    }

    /**
     * Returns the HTTP method of this request.
     *
     * @return the HTTP method (e.g., "GET", "POST")
     */
    public String getMethod() {
        return method;
    }

    /**
     * Returns the request path (without query string).
     *
     * @return the request path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns an unmodifiable view of the query parameters.
     *
     * @return the query parameter map
     */
    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    /**
     * Returns the value of a specific HTTP header.
     *
     * @param name the header name (case-insensitive lookup recommended)
     * @return the header value, or null if not present
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

    /**
     * Returns an unmodifiable view of the HTTP headers.
     *
     * @return the headers map
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /**
     * Returns the request body.
     *
     * @return the body string
     */
    public String getBody() {
        return body;
    }

    /**
     * Parses a raw query string into a map of key-value pairs.
     * 
     * <p>Handles URL-encoded query strings like {@code name=Pedro&age=25}.</p>
     *
     * @param queryString the raw query string (without the leading '?')
     * @return a map of parameter names to values
     */
    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = decodeUrlComponent(keyValue[0]);
            String value = keyValue.length > 1 ? decodeUrlComponent(keyValue[1]) : "";
            params.put(key, value);
        }
        return params;
    }

    /**
     * Decodes a URL-encoded string component.
     * Handles common encodings like %20 for spaces and + for spaces.
     *
     * @param component the URL-encoded string
     * @return the decoded string
     */
    private static String decodeUrlComponent(String component) {
        try {
            return java.net.URLDecoder.decode(component, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return component;
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", queryParams=" + queryParams +
                '}';
    }
}
