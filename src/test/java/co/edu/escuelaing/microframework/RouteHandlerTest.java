package co.edu.escuelaing.microframework;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link RouteHandler} class.
 * 
 * Tests cover route registration, lookup, normalization,
 * and edge cases.
 * 
 * @author Andersson David Sanchez Mendez
 */
public class RouteHandlerTest {

    private RouteHandler routeHandler;

    @Before
    public void setUp() {
        routeHandler = new RouteHandler();
    }

    // ==============================
    // Route registration tests
    // ==============================

    @Test
    public void testAddGetRouteIncreasesCount() {
        assertEquals(0, routeHandler.getRouteCount());
        routeHandler.addGetRoute("/hello", (req, resp) -> "Hello");
        assertEquals(1, routeHandler.getRouteCount());
    }

    @Test
    public void testAddMultipleRoutes() {
        routeHandler.addGetRoute("/hello", (req, resp) -> "Hello");
        routeHandler.addGetRoute("/pi", (req, resp) -> "3.14");
        routeHandler.addGetRoute("/time", (req, resp) -> "now");
        assertEquals(3, routeHandler.getRouteCount());
    }

    // ==============================
    // Route lookup tests
    // ==============================

    @Test
    public void testFindHandlerReturnsHandler() {
        routeHandler.addGetRoute("/hello", (req, resp) -> "Hello World");
        RestHandler handler = routeHandler.findHandler("GET", "/hello");
        assertNotNull(handler);
    }

    @Test
    public void testFindHandlerExecutes() {
        routeHandler.addGetRoute("/hello", (req, resp) -> "Hello World");
        RestHandler handler = routeHandler.findHandler("GET", "/hello");
        assertEquals("Hello World", handler.handle(null, null));
    }

    @Test
    public void testFindHandlerReturnsNullForUnregistered() {
        RestHandler handler = routeHandler.findHandler("GET", "/nonexistent");
        assertNull(handler);
    }

    @Test
    public void testFindHandlerReturnsNullForWrongMethod() {
        routeHandler.addGetRoute("/hello", (req, resp) -> "Hello");
        RestHandler handler = routeHandler.findHandler("POST", "/hello");
        assertNull(handler);
    }

    @Test
    public void testFindHandlerCaseInsensitiveMethod() {
        routeHandler.addGetRoute("/hello", (req, resp) -> "Hello");
        RestHandler handler = routeHandler.findHandler("get", "/hello");
        assertNotNull(handler);
    }

    // ==============================
    // hasRoute tests
    // ==============================

    @Test
    public void testHasRouteTrue() {
        routeHandler.addGetRoute("/hello", (req, resp) -> "Hello");
        assertTrue(routeHandler.hasRoute("GET", "/hello"));
    }

    @Test
    public void testHasRouteFalse() {
        assertFalse(routeHandler.hasRoute("GET", "/hello"));
    }

    // ==============================
    // Path normalization tests
    // ==============================

    @Test
    public void testPathWithoutLeadingSlash() {
        routeHandler.addGetRoute("hello", (req, resp) -> "Hello");
        assertNotNull(routeHandler.findHandler("GET", "/hello"));
    }

    @Test
    public void testPathWithTrailingSlash() {
        routeHandler.addGetRoute("/hello/", (req, resp) -> "Hello");
        assertNotNull(routeHandler.findHandler("GET", "/hello"));
    }

    @Test
    public void testRootPath() {
        routeHandler.addGetRoute("/", (req, resp) -> "Root");
        RestHandler handler = routeHandler.findHandler("GET", "/");
        assertNotNull(handler);
        assertEquals("Root", handler.handle(null, null));
    }

    @Test
    public void testEmptyPath() {
        routeHandler.addGetRoute("", (req, resp) -> "Root");
        RestHandler handler = routeHandler.findHandler("GET", "/");
        assertNotNull(handler);
    }

    @Test
    public void testNullPath() {
        routeHandler.addGetRoute(null, (req, resp) -> "Root");
        RestHandler handler = routeHandler.findHandler("GET", "/");
        assertNotNull(handler);
    }

    // ==============================
    // Lambda execution tests
    // ==============================

    @Test
    public void testLambdaWithRequestParam() {
        routeHandler.addGetRoute("/greet", (req, resp) -> {
            if (req != null) {
                return "Hello " + req.getValues("name");
            }
            return "Hello";
        });

        RestHandler handler = routeHandler.findHandler("GET", "/greet");
        Request req = new Request("GET", "/greet",
                java.util.Collections.singletonMap("name", "Pedro"), null, "");
        assertEquals("Hello Pedro", handler.handle(req, null));
    }

    @Test
    public void testLambdaWithComputation() {
        routeHandler.addGetRoute("/pi", (req, resp) -> String.valueOf(Math.PI));
        RestHandler handler = routeHandler.findHandler("GET", "/pi");
        assertEquals(String.valueOf(Math.PI), handler.handle(null, null));
    }

    @Test
    public void testLambdaWithMultiLineLogic() {
        routeHandler.addGetRoute("/compute", (req, resp) -> {
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
            }
            return String.valueOf(sum);
        });

        RestHandler handler = routeHandler.findHandler("GET", "/compute");
        assertEquals("55", handler.handle(null, null));
    }

    // ==============================
    // getRoutes() test
    // ==============================

    @Test
    public void testGetRoutesReturnsCopy() {
        routeHandler.addGetRoute("/hello", (req, resp) -> "Hello");
        var routes = routeHandler.getRoutes();
        routes.clear(); // clearing copy should not affect original
        assertEquals(1, routeHandler.getRouteCount());
    }
}
