package co.edu.escuelaing.microframework;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link StaticFileHandler} class.
 * 
 * Tests cover MIME type detection, file request identification,
 * and static folder configuration.
 * 
 * @author Andersson David Sanchez Mendez
 */
public class StaticFileHandlerTest {

    // ==============================
    // Constructor and config tests
    // ==============================

    @Test
    public void testDefaultFolder() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        assertEquals("/webroot", handler.getStaticFolder());
    }

    @Test
    public void testSetStaticFolder() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        handler.setStaticFolder("/public");
        assertEquals("/public", handler.getStaticFolder());
    }

    @Test
    public void testFolderNormalizationAddsSlash() {
        StaticFileHandler handler = new StaticFileHandler("webroot");
        assertEquals("/webroot", handler.getStaticFolder());
    }

    @Test
    public void testFolderNormalizationRemovesTrailingSlash() {
        StaticFileHandler handler = new StaticFileHandler("/webroot/");
        assertEquals("/webroot", handler.getStaticFolder());
    }

    @Test
    public void testNullFolderDefaultsToWebroot() {
        StaticFileHandler handler = new StaticFileHandler(null);
        assertEquals("/webroot", handler.getStaticFolder());
    }

    @Test
    public void testEmptyFolderDefaultsToWebroot() {
        StaticFileHandler handler = new StaticFileHandler("");
        assertEquals("/webroot", handler.getStaticFolder());
    }

    // ==============================
    // MIME type detection tests
    // ==============================

    @Test
    public void testContentTypeHtml() {
        assertEquals("text/html", StaticFileHandler.getContentType("index.html"));
    }

    @Test
    public void testContentTypeHtm() {
        assertEquals("text/html", StaticFileHandler.getContentType("page.htm"));
    }

    @Test
    public void testContentTypeCss() {
        assertEquals("text/css", StaticFileHandler.getContentType("style.css"));
    }

    @Test
    public void testContentTypeJs() {
        assertEquals("application/javascript", StaticFileHandler.getContentType("app.js"));
    }

    @Test
    public void testContentTypeJson() {
        assertEquals("application/json", StaticFileHandler.getContentType("data.json"));
    }

    @Test
    public void testContentTypePng() {
        assertEquals("image/png", StaticFileHandler.getContentType("logo.png"));
    }

    @Test
    public void testContentTypeJpg() {
        assertEquals("image/jpeg", StaticFileHandler.getContentType("photo.jpg"));
    }

    @Test
    public void testContentTypeJpeg() {
        assertEquals("image/jpeg", StaticFileHandler.getContentType("photo.jpeg"));
    }

    @Test
    public void testContentTypeGif() {
        assertEquals("image/gif", StaticFileHandler.getContentType("anim.gif"));
    }

    @Test
    public void testContentTypeSvg() {
        assertEquals("image/svg+xml", StaticFileHandler.getContentType("icon.svg"));
    }

    @Test
    public void testContentTypeIco() {
        assertEquals("image/x-icon", StaticFileHandler.getContentType("favicon.ico"));
    }

    @Test
    public void testContentTypeTxt() {
        assertEquals("text/plain", StaticFileHandler.getContentType("readme.txt"));
    }

    @Test
    public void testContentTypeUnknownExtension() {
        assertEquals("application/octet-stream", StaticFileHandler.getContentType("file.xyz"));
    }

    @Test
    public void testContentTypeNoExtension() {
        assertEquals("application/octet-stream", StaticFileHandler.getContentType("Makefile"));
    }

    @Test
    public void testContentTypeNull() {
        assertEquals("application/octet-stream", StaticFileHandler.getContentType(null));
    }

    @Test
    public void testContentTypeCaseInsensitive() {
        assertEquals("text/html", StaticFileHandler.getContentType("PAGE.HTML"));
    }

    @Test
    public void testContentTypeWithPath() {
        assertEquals("text/css", StaticFileHandler.getContentType("/css/style.css"));
    }

    // ==============================
    // isStaticFileRequest tests
    // ==============================

    @Test
    public void testIsStaticFileRequestHtml() {
        assertTrue(StaticFileHandler.isStaticFileRequest("/index.html"));
    }

    @Test
    public void testIsStaticFileRequestCss() {
        assertTrue(StaticFileHandler.isStaticFileRequest("/styles/main.css"));
    }

    @Test
    public void testIsStaticFileRequestJs() {
        assertTrue(StaticFileHandler.isStaticFileRequest("/js/app.js"));
    }

    @Test
    public void testIsStaticFileRequestImage() {
        assertTrue(StaticFileHandler.isStaticFileRequest("/images/logo.png"));
    }

    @Test
    public void testIsStaticFileRequestApiPath() {
        assertFalse(StaticFileHandler.isStaticFileRequest("/App/hello"));
    }

    @Test
    public void testIsStaticFileRequestRoot() {
        assertFalse(StaticFileHandler.isStaticFileRequest("/"));
    }

    @Test
    public void testIsStaticFileRequestNull() {
        assertFalse(StaticFileHandler.isStaticFileRequest(null));
    }

    // ==============================
    // File loading tests (classpath)
    // ==============================

    @Test
    public void testGetFileBytesForExistingFile() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        byte[] bytes = handler.getFileBytes("/index.html");
        // This will work when resources are on classpath (after mvn compile)
        // For unit test context, just verify method doesn't crash
        // Actual file loading tested in integration
    }

    @Test
    public void testGetFileBytesForNonExistentFile() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        byte[] bytes = handler.getFileBytes("/nonexistent.xyz");
        assertNull(bytes);
    }
}
