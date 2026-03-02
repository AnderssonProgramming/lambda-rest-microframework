package co.edu.escuelaing.microframework;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link MicroServer} class.
 * 
 * Tests cover route registration, static file configuration,
 * and the singleton pattern.
 * 
 * @author Andersson David Sanchez Mendez
 */
public class MicroServerTest {

    @Before
    public void setUp() {
        MicroServer.reset(); // Reset singleton for clean test state
    }

    @After
    public void tearDown() {
        MicroServer.reset();
    }

    // ==============================
    // Singleton tests
    // ==============================

    @Test
    public void testGetInstanceNotNull() {
        assertNotNull(MicroServer.getInstance());
    }

    @Test
    public void testGetInstanceReturnsSameInstance() {
        MicroServer instance1 = MicroServer.getInstance();
        MicroServer instance2 = MicroServer.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testResetCreatesNewInstance() {
        MicroServer instance1 = MicroServer.getInstance();
        MicroServer.reset();
        MicroServer instance2 = MicroServer.getInstance();
        assertNotSame(instance1, instance2);
    }

    // ==============================
    // Route registration tests
    // ==============================

    @Test
    public void testGetRegistersRoute() {
        MicroServer.get("/hello", (req, resp) -> "Hello World");
        RouteHandler handler = MicroServer.getInstance().getRouteHandler();
        assertTrue(handler.hasRoute("GET", "/hello"));
    }

    @Test
    public void testGetRegistersMultipleRoutes() {
        MicroServer.get("/hello", (req, resp) -> "Hello");
        MicroServer.get("/pi", (req, resp) -> String.valueOf(Math.PI));
        MicroServer.get("/time", (req, resp) -> "now");
        assertEquals(3, MicroServer.getInstance().getRouteHandler().getRouteCount());
    }

    @Test
    public void testRegisteredHandlerExecutes() {
        MicroServer.get("/hello", (req, resp) -> "Hello World");
        RestHandler handler = MicroServer.getInstance().getRouteHandler()
                .findHandler("GET", "/hello");
        assertNotNull(handler);
        assertEquals("Hello World", handler.handle(null, null));
    }

    @Test
    public void testRegisteredHandlerWithQueryParam() {
        MicroServer.get("/greet", (req, resp) -> "Hello " + req.getValues("name"));
        RestHandler handler = MicroServer.getInstance().getRouteHandler()
                .findHandler("GET", "/greet");

        Request req = new Request("GET", "/greet",
                java.util.Collections.singletonMap("name", "Pedro"), null, "");
        assertEquals("Hello Pedro", handler.handle(req, null));
    }

    @Test
    public void testRegisteredHandlerPi() {
        MicroServer.get("/pi", (req, resp) -> String.valueOf(Math.PI));
        RestHandler handler = MicroServer.getInstance().getRouteHandler()
                .findHandler("GET", "/pi");
        assertEquals(String.valueOf(Math.PI), handler.handle(null, null));
    }

    // ==============================
    // Static files configuration tests
    // ==============================

    @Test
    public void testDefaultStaticFolder() {
        StaticFileHandler sfh = MicroServer.getInstance().getStaticFileHandler();
        assertEquals("/webroot", sfh.getStaticFolder());
    }

    @Test
    public void testStaticfilesChangesFolder() {
        MicroServer.staticfiles("/public");
        StaticFileHandler sfh = MicroServer.getInstance().getStaticFileHandler();
        assertEquals("/public", sfh.getStaticFolder());
    }

    @Test
    public void testStaticfilesWithCustomPath() {
        MicroServer.staticfiles("/webroot/public");
        StaticFileHandler sfh = MicroServer.getInstance().getStaticFileHandler();
        assertEquals("/webroot/public", sfh.getStaticFolder());
    }

    // ==============================
    // REST prefix test
    // ==============================

    @Test
    public void testGetRestPrefix() {
        assertEquals("/App", MicroServer.getRestPrefix());
    }
}
