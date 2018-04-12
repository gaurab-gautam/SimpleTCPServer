/**
 *
 * @author Gaurab R. Gautam
 */

// Package name
package MultiThreadTCPServer;

// Imported libraries
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

/**
 * Name:        MessageHandler
 * Type:        Class
 * Description: This Class helps to compose and send messages
 */
public class MessasgeHandler 
{
    /**
     * Name       : sendMsg_301
     * Input      : filePath as Path; filename as String,
     *              outToClient as DataOutputStream
     * Output     : none
     * Description: Send Message 301 - Moved Permanently
     * @param filename
     * @param newAddr
     * @param outToClient
     * @throws java.io.IOException
     */
    public static void sendMsg_301(String filename, String newAddr, 
            DataOutputStream outToClient) throws IOException
    {
        // Show error message for base-html file request
        if (Utilities.getContentType(filename).equals("text/html"))
        {
            // write error message with error code, phrase and custom
            // html message
            String responseMsg = Constants.HTTP_RESPONSE_LINE_MOVED_PERMANENTLY; 
            responseMsg += "Location: " + newAddr + "\r\n";
            responseMsg += Constants.HTTP_RESPONSE_HEADER_CONTENT_TYPE_HTML;
            responseMsg += ComposeMsg_301();

            // Write message to the socket and send it to the client
            outToClient.writeBytes(responseMsg);
        }
    }
    
    /**
     * Name       : sendErrorMsg_400
     * Input      : filePath as Path; filename as String,
     *              outToClient as DataOutputStream
     * Output     : none
     * Description: Send Error Message 400 - Bad Request to client socket
     * @param outToClient
     * @throws java.io.IOException
     */
    public static void sendErrorMsg_400(String filename, 
            DataOutputStream outToClient) throws IOException
    {
        // write error message with error code, phrase and custom
        // html message
        String responseMsg = Constants.HTTP_RESPONSE_LINE_BAD_REQUEST;
        responseMsg += Constants.HTTP_RESPONSE_HEADER_CONTENT_TYPE_HTML;
        responseMsg += composeErrorMsg_400();

        // log the responsse message for debugging
        //WriteMsgToFile(responseMsg);

        // Write message to the socket and send it to the client
        outToClient.writeBytes(responseMsg);
    }
    
    /**
     * Name       : sendErrorMsg_404
     * Input      : filePath as Path; filename as String,
     *              outToClient as DataOutputStream
     * Output     : none
     * Description: Send Error Message 404 - Not Found to client socket
     * @param filePath
     * @param filename
     * @param outToClient
     * @throws java.io.IOException
     */
    public static void sendErrorMsg_404(Path filePath, String filename, 
            DataOutputStream outToClient) throws IOException
    {
        // Show error message for base-html file request
        if (Utilities.getContentType(filename).equals("text/html"))
        {
            // write error message with error code, phrase and custom
            // html message
            String responseMsg = Constants.HTTP_RESPONSE_LINE_NOT_FOUND;
            responseMsg += Constants.HTTP_RESPONSE_HEADER_CONTENT_TYPE_HTML;
            responseMsg += composeErrorMsg_404();

            // log the responsse message for debugging
            //WriteMsgToFile(responseMsg);

            // Write message to the socket and send it to the client
            outToClient.writeBytes(responseMsg);
        }
    }
    
    /**
     * Name       : sendHtmlResponseMsg
     * Input      : filePath as Path; outToClient as DataOutputStream
     * Output     : none
     * Description: Write Response Message with HTML data to client socket
     * @param filePath
     * @param outToClient
     */
    public static void sendHtmlResponseMsg(Path filePath, DataOutputStream outToClient)
    {
        // Html data to be sent to the client
        String data = "";
        
        // Response message to send to the client, excluding data
        String responseMsg = "";
        
        // write response message
        responseMsg = Constants.HTTP_RESPONSE_LINE_OK;
        responseMsg += Constants.HTTP_RESPONSE_HEADER_CONTENT_TYPE_HTML;

        // Create an input stream to read the requested file
        // Buffered reader will read the file
        try (InputStream in = Files.newInputStream(filePath);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in))) 
        {
            // Stores each line of the file
            String line = null;

            // Read each line until end of line
            while ((line = reader.readLine()) != null) 
            {
                // Get resource content from the file
                data += line;
            }

            // Send response message including data to the client
            outToClient.writeBytes(responseMsg + data);
        }

        // Catch an IO Exception and print it
        catch (IOException e)   
        {
            System.err.println(e);
        }
    }
    
    /**
     * Name       : sendImageResponseMsg
     * Input      : filePath as Path; filename as String, 
     *              outToClient as DataOutputStream
     * Output     : none
     * Description: Write Response Message with Image data to client socket
     * @param filePath
     * @param filename
     * @param outToClient
     */
    public static void sendImageResponseMsg(Path filePath, String filename,
            DataOutputStream outToClient)
    {
        // Reads image data
        BufferedImage imageData = null;
         
        // Response message to send to the client, excluding data
        String responseMsg = "";
                
        // write response message
        responseMsg = Constants.HTTP_RESPONSE_LINE_OK;
        responseMsg += Constants.HTTP_RESPONSE_HEADER_CONTENT_TYPE_IMAGE;

        try  
        {
            // Read the image file and put the data in buffered image
            String fileExt = filename.substring(filename.indexOf(".") + 1);
            imageData = ImageIO.read(new File(filePath.toString()));

            // Send response message excluding image data to the client
            outToClient.writeBytes(responseMsg);

            // Now send image data to the client
            ImageIO.write(imageData, fileExt, outToClient);
        }

        // Catch an IO Exception and print it
        catch (IOException e)   
        {
            System.err.println(e);
        }
    }
    
    
    
    /**
     * Name       : composeMsg_301
     * Input      : none
     * Output     : Message as string
     * Description: Compose the 301 message : Moved Permanently
     * @return message
     */
    private static String composeMsg_301()
    {
        // compose html for Error 301
        String data = "";
        
        // Create an input stream to read the requested file
        // Buffered reader will read the file
        try (InputStream in = Files.newInputStream(Paths.get(
                Constants.HTTP_MOVED_PERMANENTLY_MESSAGE_HTML_FILE));
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in))) 
        {
            // Stores each line of the file
            String line = null;

            // Read each line until end of line
            while ((line = reader.readLine()) != null) 
            {
                // Get resource content from the file
                data += line;
            }
        }

        // Catch an IO Exception and print it
        catch (IOException e)   
        {
            System.err.println(e);
        }
        
        // Return Message
        return (data);
    }
    
    
    /**
     * Name       : composeErrorMsg_404
     * Input      : none
     * Output     : Error Message as string
     * Description: Compose the error message 404: Not Found
     * @return message
     */
    private static String composeErrorMsg_404()
    {
        // compose html for Error 404
        String data = "";
        
        // Create an input stream to read the requested file
        // Buffered reader will read the file
        try (InputStream in = Files.newInputStream(Paths.get(
            Constants.HTTP_ERROR_404_HTML_FILE));
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in))) 
        {
            // Stores each line of the file
            String line = null;

            // Read each line until end of line
            while ((line = reader.readLine()) != null) 
            {
                // Get resource content from the file
                data += line;
            }
        }

        // Catch an IO Exception and print it
        catch (IOException e)   
        {
            System.err.println(e);
        }
        
        // Return Message
        return (data);
    }
    
    /**
     * Name       : composeErrorMsg_400
     * Input      : none
     * Output     : Error Message as string
     * Description: Compose the error message 400: Bad Request
     * @return message
     */
    private static String composeErrorMsg_400()
    {
        // compose html for Error 400
        String data = "";
        
        // Create an input stream to read the requested file
        // Buffered reader will read the file
        try (InputStream in = Files.newInputStream(Paths.get(
            Constants.HTTP_ERROR_400_HTML_FILE));
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in))) 
        {
            // Stores each line of the file
            String line = null;

            // Read each line until end of line
            while ((line = reader.readLine()) != null) 
            {
                // Get resource content from the file
                data += line;
            }
        }

        // Catch an IO Exception and print it
        catch (IOException e)   
        {
            System.err.println(e);
        }
        
        // Return Message
        return (data);
    }
}
