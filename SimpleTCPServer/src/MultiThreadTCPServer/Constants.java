/**
 *
 * @author Gaurab R. Gautam
 */

// Package name
package MultiThreadTCPServer;

/**
 * Name:        Constants
 * Type:        Class
 * Description: Class that constants several constants used in the program
 */
final public class Constants 
{
    final public static int PORT = 6789;
    final public static int BUFFER_SIZE = 1024;
    final public static String DEFAULT_FILE_PATH = "index.html";
    final public static String DEFAULT_FILE_NAME = "index.html";
    final public static String BASE_URL = "http://127.0.0.1:6789";
    final public static String BASE_URL_NO_PROTOCOL = "127.0.0.1:6789";
    
    // HTTP response line, with codes and status
    final public static String HTTP_RESPONSE_LINE_OK = "HTTP/1.1 200 OK\r\n";
    final public static String HTTP_RESPONSE_LINE_NOT_FOUND = "HTTP/1.1 404 Not Found\r\n";
    final public static String HTTP_RESPONSE_LINE_BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\n";
    final public static String HTTP_RESPONSE_LINE_MOVED_PERMANENTLY = "HTTP/1.1 301 Moved Permanently\r\n";
    
    final public static String HTTP_RESPONSE_HEADER_CONTENT_TYPE_HTML ="Content-Type: text/html\r\n\r\n";
    final public static String HTTP_RESPONSE_HEADER_CONTENT_TYPE_IMAGE ="Content-Type: image\r\n\r\n";
    
    // HTML files to show (error) messages
    final public static String HTTP_MOVED_PERMANENTLY_MESSAGE_HTML_FILE = "./errorfiles/MovedPermanently-301.html";
    final public static String HTTP_ERROR_404_HTML_FILE = "./errorfiles/Error-404.html";
    final public static String HTTP_ERROR_400_HTML_FILE = "./errorfiles/Error-400.html";
    
    final public static String PERMANENTLY_MOVED_FILES_RECORDS = "./movedfileslog/movedfiles.log";
    final public static String PERMANENTLY_MOVED_FILES_MAPPING_SEPARATOR = "->";
}
