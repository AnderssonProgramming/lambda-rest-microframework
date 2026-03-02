package co.edu.escuelaing.microframework;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles serving static files (HTML, CSS, JavaScript, images, etc.)
 * from a specified directory within the classpath.
 * 
 * <p>The static file directory is configured via {@link MicroServer#staticfiles(String)}.
 * Files are looked up first in the classpath (inside target/classes/) and then
 * on the filesystem as a fallback.</p>
 * 
 * <p>Supports common MIME types for web development including HTML, CSS, JS,
 * PNG, JPG, GIF, SVG, ICO, and JSON.</p>
 * 
 * @author Andersson David Sanchez Mendez
 * @version 1.0
 */
public class StaticFileHandler {

    private static final Logger LOGGER = Logger.getLogger(StaticFileHandler.class.getName());
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        // Text-based MIME types
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("xml", "application/xml");
        MIME_TYPES.put("txt", "text/plain");

        // Image MIME types
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("webp", "image/webp");

        // Font MIME types
        MIME_TYPES.put("woff", "font/woff");
        MIME_TYPES.put("woff2", "font/woff2");
        MIME_TYPES.put("ttf", "font/ttf");
    }

    private String staticFolder;

    /**
     * Constructs a new StaticFileHandler with the specified directory.
     *
     * @param staticFolder the directory path relative to classpath (e.g., "/webroot")
     */
    public StaticFileHandler(String staticFolder) {
        this.staticFolder = normalizeFolderPath(staticFolder);
    }

    /**
     * Gets the configured static folder path.
     *
     * @return the static folder path
     */
    public String getStaticFolder() {
        return staticFolder;
    }

    /**
     * Sets the static files directory.
     *
     * @param folder the directory path relative to classpath
     */
    public void setStaticFolder(String folder) {
        this.staticFolder = normalizeFolderPath(folder);
    }

    /**
     * Attempts to serve a static file for the given request path.
     * 
     * <p>The method looks for the file in the classpath under the configured
     * static folder. It returns the file contents as a byte array, or null
     * if the file is not found.</p>
     *
     * @param requestPath the request URI path (e.g., "/index.html")
     * @return the file contents as byte array, or null if not found
     */
    public byte[] getFileBytes(String requestPath) {
        String filePath = staticFolder + requestPath;
        LOGGER.log(Level.FINE, "Looking for static file: {0}", filePath);

        // Try classpath first
        try {
            URL resource = getClass().getClassLoader().getResource(
                    filePath.startsWith("/") ? filePath.substring(1) : filePath);
            if (resource != null) {
                try (InputStream is = resource.openStream()) {
                    return readAllBytes(is);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error reading classpath resource: {0}", e.getMessage());
        }

        // Fallback: try filesystem (for development mode)
        File file = new File("src/main/resources" + filePath);
        if (file.exists() && file.isFile()) {
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error reading file from filesystem: {0}", e.getMessage());
            }
        }

        return null;
    }

    /**
     * Determines the MIME content type based on the file extension.
     *
     * @param fileName the file name or path
     * @return the MIME type string, defaulting to "application/octet-stream"
     */
    public static String getContentType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "application/octet-stream";
        }
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
    }

    /**
     * Checks if the given path corresponds to a file (has a file extension).
     *
     * @param path the request path
     * @return true if the path appears to reference a static file
     */
    public static boolean isStaticFileRequest(String path) {
        if (path == null) return false;
        String lastSegment = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
        return lastSegment.contains(".");
    }

    /**
     * Normalizes the folder path for consistent lookups.
     *
     * @param folder the raw folder path
     * @return the normalized folder path
     */
    private String normalizeFolderPath(String folder) {
        if (folder == null || folder.isEmpty()) {
            return "/webroot";
        }
        if (!folder.startsWith("/")) {
            folder = "/" + folder;
        }
        if (folder.endsWith("/")) {
            folder = folder.substring(0, folder.length() - 1);
        }
        return folder;
    }

    /**
     * Reads all bytes from an InputStream.
     *
     * @param is the input stream
     * @return byte array of all data
     * @throws IOException if an I/O error occurs
     */
    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[4096];
        while ((bytesRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
