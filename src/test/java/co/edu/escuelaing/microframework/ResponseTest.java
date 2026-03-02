package co.edu.escuelaing.microframework;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Response} class.
 * 
 * Tests cover status codes, content types, headers,
 * and the fluent API.
 * 
 * @author Andersson David Sanchez Mendez
 */
public class ResponseTest {

    private Response response;

    @Before
    public void setUp() {
        response = new Response();
    }

    // ==============================
    // Default values tests
    // ==============================

    @Test
    public void testDefaultStatusCode() {
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testDefaultContentType() {
        assertEquals("text/html", response.getContentType());
    }

    @Test
    public void testDefaultBody() {
        assertEquals("", response.getBody());
    }

    @Test
    public void testDefaultHeadersEmpty() {
        assertTrue(response.getHeaders().isEmpty());
    }

    // ==============================
    // Setter/Getter tests
    // ==============================

    @Test
    public void testSetStatusCode() {
        response.setStatusCode(404);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void testSetContentType() {
        response.setContentType("application/json");
        assertEquals("application/json", response.getContentType());
    }

    @Test
    public void testSetBody() {
        response.setBody("Hello World");
        assertEquals("Hello World", response.getBody());
    }

    @Test
    public void testSetHeader() {
        response.setHeader("X-Custom", "value");
        assertEquals("value", response.getHeaders().get("X-Custom"));
    }

    // ==============================
    // Fluent API tests
    // ==============================

    @Test
    public void testFluentApiChaining() {
        Response result = response
                .setStatusCode(201)
                .setContentType("application/json")
                .setBody("{\"key\": \"value\"}")
                .setHeader("X-Request-Id", "123");

        assertSame(response, result);
        assertEquals(201, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{\"key\": \"value\"}", response.getBody());
        assertEquals("123", response.getHeaders().get("X-Request-Id"));
    }

    // ==============================
    // Status message tests
    // ==============================

    @Test
    public void testStatusMessage200() {
        response.setStatusCode(200);
        assertEquals("OK", response.getStatusMessage());
    }

    @Test
    public void testStatusMessage404() {
        response.setStatusCode(404);
        assertEquals("Not Found", response.getStatusMessage());
    }

    @Test
    public void testStatusMessage500() {
        response.setStatusCode(500);
        assertEquals("Internal Server Error", response.getStatusMessage());
    }

    @Test
    public void testStatusMessage201() {
        response.setStatusCode(201);
        assertEquals("Created", response.getStatusMessage());
    }

    @Test
    public void testStatusMessage400() {
        response.setStatusCode(400);
        assertEquals("Bad Request", response.getStatusMessage());
    }

    @Test
    public void testStatusMessage403() {
        response.setStatusCode(403);
        assertEquals("Forbidden", response.getStatusMessage());
    }

    @Test
    public void testStatusMessage405() {
        response.setStatusCode(405);
        assertEquals("Method Not Allowed", response.getStatusMessage());
    }

    @Test
    public void testStatusMessageUnknown() {
        response.setStatusCode(999);
        assertEquals("Unknown", response.getStatusMessage());
    }
}
