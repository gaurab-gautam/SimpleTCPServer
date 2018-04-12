/**
 *
 * @author Gaurab R. Gautam
 */


// Package name
package MultiThreadTCPServer;

// Imported libraries
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Name:        Utilities
 * Type:        Class
 * Description: Utility class that contains different helper functions
 */
public class Utilities 
{
    /**
     * Name       : isFilePermanentlyMoved
     * Input      : filePath as Path
     * Output     : string or null
     * Description: Returns file new location if file has been permanently moved
     *              to a new location, otherwise, return null
     * @param filename
     * @param url
     * @return string/null
     */
    public static String isFileMovedPermanently(String filename, String url)
    {
        // Show error message for base-html file request
        if (getContentType(filename).equals("text/html"))
        {
            // Create an input stream to read the requested file
            // Buffered reader will read the file
            try (InputStream in = Files.newInputStream(Paths.get(
                Constants.PERMANENTLY_MOVED_FILES_RECORDS));
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in))) 
            {
                // Stores each line of the file
                String line = null;

                // Read each line until end of line
                while ((line = reader.readLine()) != null) 
                {
                    // Match source vs destination path to see if the file has 
                    // been moved
                    String urls[] = line.split(Constants.PERMANENTLY_MOVED_FILES_MAPPING_SEPARATOR);
                    if (urls[0].trim().equals(url.trim()))
                    {
                        return urls[1];
                    }
                }
            }
            // Catch an IO Exception and print it
            catch (IOException e)   
            {
                e.printStackTrace(System.out);
            }
        }
        // file hasn't been moved permanently
        return null;
    }
    
    /**
     * Name       : checkForRequestMsgErrors
     * Input      : requestMsg as string
     * Output     : True on error, false otherwise
     * Description: Checks to see if there are any errors in request message
     * @param requestMsg
     * @return true/false
     */
    public static boolean checkForRequestMsgErrors(String [] requestMsg)
    {
        // Check if the last is blank
        // We assume that there will be no data sent to the server with the 
        // request message
        boolean isHeaderDataSeparatorExist = 
                ((requestMsg[requestMsg.length -1]).trim().equals(""));
             
        // Get the Host header line from request
        String hostHeaderLine = FindHostHeaderLine(requestMsg);
        
        // Get the host url
        //String url = hostHeaderLine.substring(hostHeaderLine.indexOf(":") + 1).trim();
        
        // Check for errors
        return ((checkForRequestLineErrors(requestMsg[0])) ||
                (checkForHostHeaderLineErrors(hostHeaderLine)) ||
                (checkForHeaderFieldsError(requestMsg)) ||
                (!isHeaderDataSeparatorExist));
    }
    
    /**
     * Name       : checkForHeaderFieldsErrors
     * Input      : headers as array of string
     * Output     : True on error, otherwise false
     * Description: Checks to see if there are any errors on headers
     * @param requestMsg
     * @return true/false
     */
    private static boolean checkForHeaderFieldsError(String [] headers)
    {
        // Check to make sure only one host header exists
        // and no space between 'Host' and ':'
        // Skip the first request line and empty line at the end
        for (int i = 1; i < (headers.length - 2); i++)
        {
            // Get the header field part of header line
            String field = (headers[i].split(" "))[0].trim();
            
            // Check the header field type
            switch(field)
            {
                // Some of the allowed header, continue checking
                case "Accept:":
                case "Accept-Charset:":
                case "Accept-Encoding:":
                case "Accept-Language:":
                case "Accept-Datetime:":
                case "Cache-Control:":
                case "Connection:":
                case "Cookie:":
                case "Content-Length:":
                case "Content-MD5:":
                case "Content-Type:":
                case "Date:":
                case "Expect:":
                case "From:":
                case "Host:":
                case "If-Match:":
                case "If-Modified-Since:":
                case "If-Non-Match:":
                case "If-Range:":
                case "If-Unmodified-Since:":
                case "Max-Forwards:":
                case "Origins:":
                case "Pragma:":
                case "Proxy-Authorization:":
                case "Proxy-Connection:":
                case "Range:":
                case "Referer:":
                case "TE:":
                case "User-Agent:":
                case "Via:":
                case "Warning":
                    break;
                    
                // Error found; return true
                default:
                    return true;
            }
        }

        // No error on host header lines: return false
        return false;
    }
    
    /**
     * Name       : findHostHeaderLine
     * Input      : requestMsg as array of strings
     * Output     : Host header as string
     * Description: Returns the header line that contains host information
     * @param requestMsg
     * @return string/null
     */
    public static String findHostHeaderLine(String [] requestMsg)
    {
        // Record host header count
        int hostHeaderCount = 0;
        String hostHeader = "";
        
        // Check to make sure only one host header exists
        // and no space between 'Host' and ':'
        for (int i = 0; i < requestMsg.length; i++)
        {
            if (requestMsg[i].contains("Host:"))
            {
                // Update found host header count and get the header line
                hostHeaderCount += 1;
                hostHeader = requestMsg[i];
            }
        }
        
        // Exactly one host header line found; return that header line
        if (hostHeaderCount == 1)
        {
            return hostHeader;
        }
        
        // Error on host header line: return null
        return null;
    }
    
    /**
     * Name       : checkForHostHeaderLineErrors
     * Input      : Host Header line segments as string array
     * Output     : True on error, false otherwise
     * Description: Checks to see if there are any errors in Host Header line
     * @param args
     * @return true/false
     */
    private static boolean checkForHostHeaderLineErrors(String args)
    {        
        // Split the host header line into pieces
        String splits[] = args.split(" ");
        
        // Host header line should only contain two fields
        // And first field is 'Host:'
        if ((splits.length != 2) && (!splits[0].trim().equals("Host:")))
        {
            // Return true for error
            return true;
        }
        
        // Return error if the header contains invalid url        
        return !splits[1].trim().matches("(((http://)?([\\w-\\d]+\\.)+[\\w-\\d]+){0,1}(:\\d{4})?)");
    }
    
    /**
     * Name       : checkForRequestLineErrors
     * Input      : Request line as string
     * Output     : True on error, false otherwise
     * Description: Checks to see if there are any errors in request line
     * @param requestLine
     * @return true/false
     */
    private static boolean checkForRequestLineErrors(String requestLine)
    {
        // Split the request line and hold the data into array
        String args [] = requestLine.split(" ");
            
        // Request line should have command, url and version
        if (args.length != 3)
        {
            return true;
        }
        
        // Error on commands other than GET, POST, HEAD, PUT, DELETE
        if (!((args[0].equals("GET")) || (args[0].equals("POST"))
                || (args[0].equals("HEAD")) ||(args[0].equals("PUT"))
                || (args[0].equals("DELETE"))))
        {
            return true;
        }

        // Check if the path contain no directory or file name
        if (!args[1].trim().equals("/"))
        {
            // Regular expression to match path syntax
            String regex = "(/[\\w.]+)+/?";
            
            // Use the regular expression to math the corret path syntax
            if (!args[1].matches(regex))
            {             
                return true;
            }
        }
        
        // Support only Http/1.1 and http/1.0
        return !((args[2].equals("HTTP/1.1")) || (args[2].equals("HTTP/1.0")));
    }
    
    /**
     * Name       : getContentType
     * Input      : File name to parse to get content type; eg: html/text, image
     * Output     : Content type as string
     * Description: Parses file name to get the content type
     * @param filename
     * @return 
     */
    public static String getContentType(String filename)
    {
        // If the path contains the directory instead of file name
        // Return default html content type
        if (!filename.contains("."))
        {
            return "text/html";
        }
        
        // Switch filenames based on file extension
        // Return the content type
        switch (filename.substring(filename.indexOf(".")))
        {
            case ".html":
            case ".txt":
            case ".htm":
                return "text/html";

            case ".jpg":
            case ".jpeg":
            case ".jp3":
            case ".jfif":
            case ".png":
            case ".gif":
            case ".bmp":
            case ".dib":
            case ".tif":
            case ".tiff":
            case ".ico":
                return "image";
                
            // Also return text/html for all the other file types
            default:
                return "text/html";

        }
    }
    
    /**
     * Name       : countOccurencesOf
     * Input      : s as String, c as char
     * Output     : Number of occurrences of a char 
     * Description: Count the number of occurrences of a char in a string
     * @param s
     * @param c
     * @return integer
     */
    public static int countOccurrencesOf(String s, char c)
    {
        // Record occurrences of character in a string
        int count = 0;
        
        for (int i = 0; i < s.length(); i++)
        {
            // Check if the character in string is same as the given char
            if (s.toCharArray()[i] == c)
            {
                // Update the count
                count += 1;
            }
        }
        
        // Return the count
        return count;
    }
    
    /**
     * Name       : parseFilePath
     * Input      : Unparsed file path
     * Output     : File path with compatible format as string
     * Description: Parses file path into compatible format
     * @param path
     * @return path
     */
    public static String parseFilePath(String path)
    {
        // Remove any unnecessary ending slashes typed by the user
        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);            
        }
        
        // Make sure the path is in correct format
        if ((countOccurrencesOf(path, '/') > 1) && (countOccurrencesOf(path, '.') > 1))
        {
            path = path.substring(path.lastIndexOf("/"));
        }
        
        // Check to see if there are any parent directories lised 
        // in the path message
        if (path.substring(1).contains(("/")))
        {
            // Replace path with an IDE compatable path
            path = path.replace("/", "\\" + "\\");
            path = "." + path;
        }
        else
        {
            // No parent directory, remove path separator
            path = path.replace("/", "");
        }
      
        // Return the parsed path
        return path;
    }
    
    /**
     * Name       : getFileNameOnly
     * Input      : Complete path of the file, including name
     * Output     : File name without path info
     * Description: Gets the file name without path info
     * @param path
     * @return path
     */
    public static String getFileNameOnly(String path)
    {
        // Check to see if the path contains parent directories info
        if (path.substring(1).contains(("\\")))
        {
            // Fileer the whole path to get file name only
            // return the file name
            return path.substring(path.lastIndexOf("\\"));
        }
        else 
        {
            // return the file name
            return path;
        }
    }
}
