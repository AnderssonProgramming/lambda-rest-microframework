package co.edu.escuelaing.microframework;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP response with status code, headers, and body.
 * 
 * <p>Provides a fluent API for setting response properties within
 * REST service handlers.</p>
 * 
 * @author Andersson David Sanchez Mendez
 * @version 1.0
 */
public class Response {

    private int statusCode;
    private String contentType;
    private final Map<String, String> headers;
    private String body;

    /**
     * Constructs a new Response with default values.
     * Default status code is 200, default content type is "text/html".
     */
    public Response() {
        this.statusCode = 200;
        this.contentType = "text/html";
        this.headers = new HashMap<>();
        this.body = "";
    }

    /**
     * Gets the HTTP status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the HTTP status code.
     *
     * @param statusCode the HTTP status code
     * @return this Response for fluent chaining
     */
    public Response setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Gets the content type header value.
     *
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type header.
     *
     * @param contentType the MIME content type
     * @return this Response for fluent chaining
     */
    public Response setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Sets a custom HTTP header.
     *
     * @param name  the header name
     * @param value the header value
     * @return this Response for fluent chaining
     */
    public Response setHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    /**
     * Gets all custom headers.
     *
     * @return the headers map
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets the response body.
     *
     * @return the body string
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the response body.
     *
     * @param body the body string
     * @return this Response for fluent chaining
     */
    public Response setBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * Returns the HTTP status message for common status codes.
     *
     * @return the status message corresponding to the current status code
     */
    public String getStatusMessage() {
        switch (statusCode) {
            case 200: return "OK";
            case 201: return "Created";
            case 301: return "Moved Permanently";
            case 302: return "Found";
            case 304: return "Not Modified";
            case 400: return "Bad Request";
            case 403: return "Forbidden";
            case 404: return "Not Found";
            case 405: return "Method Not Allowed";
            case 500: return "Internal Server Error";
            default:  return "Unknown";
        }
    }
}
