package co.edu.escuelaing.microframework;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Request} class.
 * 
 * Tests cover query parameter parsing, URL decoding,
 * and request object construction.
 * 
 * @author Andersson David Sanchez Mendez
 */
public class RequestTest {

    private Request request;
    private Map<String, String> queryParams;
    private Map<String, String> headers;

    @Before
    public void setUp() {
        queryParams = new HashMap<>();
        queryParams.put("name", "Pedro");
        queryParams.put("age", "25");

        headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Content-Type", "text/html");

        request = new Request("GET", "/hello", queryParams, headers, "");
    }

    // ==============================
    // getValues() tests
    // ==============================

    @Test
    public void testGetValuesReturnsCorrectValue() {
        assertEquals("Pedro", request.getValues("name"));
    }

    @Test
    public void testGetValuesReturnsSecondParam() {
        assertEquals("25", request.getValues("age"));
    }

    @Test
    public void testGetValuesReturnsEmptyStringForMissingKey() {
        assertEquals("", request.getValues("nonexistent"));
    }

    @Test
    public void testGetValuesWithEmptyKey() {
        assertEquals("", request.getValues(""));
    }

    // ==============================
    // Constructor and getters tests
    // ==============================

    @Test
    public void testGetMethod() {
        assertEquals("GET", request.getMethod());
    }

    @Test
    public void testGetPath() {
        assertEquals("/hello", request.getPath());
    }

    @Test
    public void testGetBody() {
        assertEquals("", request.getBody());
    }

    @Test
    public void testGetHeader() {
        assertEquals("localhost:8080", request.getHeader("Host"));
    }

    @Test
    public void testGetHeaderReturnsNullForMissing() {
        assertNull(request.getHeader("X-Custom"));
    }

    @Test
    public void testGetQueryParamsIsUnmodifiable() {
        Map<String, String> params = request.getQueryParams();
        try {
            params.put("new", "value");
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testGetHeadersIsUnmodifiable() {
        Map<String, String> hdrs = request.getHeaders();
        try {
            hdrs.put("new", "value");
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testConstructorWithNullParams() {
        Request req = new Request("POST", "/test", null, null, null);
        assertEquals("POST", req.getMethod());
        assertEquals("/test", req.getPath());
        assertEquals("", req.getValues("any"));
        assertEquals("", req.getBody());
    }

    // ==============================
    // parseQueryString() tests
    // ==============================

    @Test
    public void testParseQueryStringSimple() {
        Map<String, String> params = Request.parseQueryString("name=Pedro");
        assertEquals("Pedro", params.get("name"));
        assertEquals(1, params.size());
    }

    @Test
    public void testParseQueryStringMultipleParams() {
        Map<String, String> params = Request.parseQueryString("name=Pedro&age=25&city=Bogota");
        assertEquals("Pedro", params.get("name"));
        assertEquals("25", params.get("age"));
        assertEquals("Bogota", params.get("city"));
        assertEquals(3, params.size());
    }

    @Test
    public void testParseQueryStringWithEmptyValue() {
        Map<String, String> params = Request.parseQueryString("key=");
        assertEquals("", params.get("key"));
    }

    @Test
    public void testParseQueryStringWithNoValue() {
        Map<String, String> params = Request.parseQueryString("key");
        assertEquals("", params.get("key"));
    }

    @Test
    public void testParseQueryStringNull() {
        Map<String, String> params = Request.parseQueryString(null);
        assertTrue(params.isEmpty());
    }

    @Test
    public void testParseQueryStringEmpty() {
        Map<String, String> params = Request.parseQueryString("");
        assertTrue(params.isEmpty());
    }

    @Test
    public void testParseQueryStringWithEncodedSpaces() {
        Map<String, String> params = Request.parseQueryString("name=John+Doe");
        assertEquals("John Doe", params.get("name"));
    }

    @Test
    public void testParseQueryStringWithPercentEncoding() {
        Map<String, String> params = Request.parseQueryString("greeting=Hello%20World");
        assertEquals("Hello World", params.get("greeting"));
    }

    @Test
    public void testParseQueryStringWithSpecialChars() {
        Map<String, String> params = Request.parseQueryString("email=user%40example.com");
        assertEquals("user@example.com", params.get("email"));
    }

    // ==============================
    // toString() test
    // ==============================

    @Test
    public void testToString() {
        String str = request.toString();
        assertTrue(str.contains("GET"));
        assertTrue(str.contains("/hello"));
        assertTrue(str.contains("name"));
    }
}
