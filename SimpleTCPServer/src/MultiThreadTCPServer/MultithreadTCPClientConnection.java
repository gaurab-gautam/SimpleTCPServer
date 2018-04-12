/**
 *
 * @author Gaurab R. Gautam
 */

// Package name
package MultiThreadTCPServer;

// Imported libraries
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Name:        MultithreadTCPClientConnection
 * Type:        Class
 * Description: This Class is instantiated to spurn new thread to run the 
 *              client socket connection
 */
public class MultithreadTCPClientConnection implements Runnable
{
    // Socket connection to handle data transfer
    private Socket connectionSocket = null;
    
    /**
     * Name       : run
     * Input      : none
     * Output     : none
     * Description: Runs client connection in a thread
     */
    @Override
    public void run() 
    {
        try 
        {
            // Connect to a client
            connectTCPClient();
        } 
        
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Name       : finalize
     * Input      : none
     * Output     : none
     * Description: Frees object created during thread run method
     */
    @Override
    protected void finalize()
    {
        try
        {
            // Close the socket connection if it hasn't already been closed
            if (connectionSocket != null)
                connectionSocket.close();
        } 
        
        catch (IOException e) 
        {
            // Print the error message
            System.out.println("Socket couldn't be closed! : " + e.getMessage());
        } 
        finally 
        {
            try 
            {
                super.finalize();
            } 
            catch (Throwable e) 
            {
                System.out.println(e.getMessage());
            }
        }
}
    
    /**
     * Name       : multithreadTCPClientConnection
     * Input      : Socket connected to client
     * Output     : none
     * Description: Constructor that assign client distinct connection
     * @param clientSocket
     */
    public multithreadTCPClientConnection(Socket clientSocket)
    {
        // Get the socket connected to the client
        this.connectionSocket = clientSocket;
    }
    
    /**
     * Name       : connectTCPClient
     * Input      : none
     * Output     : none
     * Description: Establishes the unique connection with a client for
     *              communication through socket
     * @throws java.lang.Exception
     */
    public void connectTCPClient() throws Exception
    {           
        // Holds client's request for resources one line at a time
        String clientRequest = "";
        
        // Reads text data
        BufferedReader inFromClient = null;
        
        // Output stream connected to socket used to send data to the client
        DataOutputStream outToClient = null;  
        
        // Holds file path to the resourses from request line
        String unparsedPath = "";
        
        // File path for the base html file or other objects
        Path filePath = null;
        
        // Filename of the resource without complete path info
        String filename = "";
        
        // Stores the request message from the client
        String requestMsg = "";
        
        // Stores each line of request message temporary
        String aLine = "";
        
        try  
        {
            // Attach input stream from socket to input stream reader and store
            // data read into a buffered reader
            inFromClient = new BufferedReader(new
                        InputStreamReader(connectionSocket.getInputStream()));
            
            // create an output stream connected to the socket
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            
            // Read each line of request message
            while ((aLine = inFromClient.readLine()) != null)
            {
                requestMsg += (aLine + "\r\n"); 
                
                // If end of header lines, exit
                if (aLine.trim().equals(""))
                    break;
            }
            
            // Parse request to indicate end of request
            requestMsg += ("     ");
            
            // Check if the message is not empty
            if (!requestMsg.trim().equals(""))
            {
                // Splits the message into lines
                String msgLines[] = requestMsg.split("\r\n");
                
                // Check if the request message has an incorrect syntax
                if (Utilities.checkForRequestMsgErrors(msgLines))
                {
                    // Throw an exception if the request message has incorrect
                    // syntax
                    throw new BadRequestException();
                }

                // Get the first line from client request message
                clientRequest = msgLines[0];

                // Split the request line and hold the data into array
                String splits[] = clientRequest.split(" ");

                // assign unparsed path from client request
                unparsedPath = splits[1];

                // Check to see if user only specify IP and Port #
                // If yes, default resource will be index.html file
                if (!unparsedPath.trim().equals("/"))
                {                    
                    // Parse the file path that is consistent with the IDE
                    String parsedPath = Utilities.parseFilePath(unparsedPath);

                    // Get the file path in standard form
                    filePath = Paths.get(parsedPath);

                    // Get the file name without path information
                    filename = Utilities.getFileNameOnly(parsedPath);
                } 
                else
                {
                    // Default base html file
                    filePath = Paths.get(Constants.DEFAULT_FILE_PATH);
                    filename = Constants.DEFAULT_FILE_NAME;
                }

                // If the requested file exists, process request
                if (Files.exists(filePath))
                {
                    // Send response message with text/html or image data
                    switch (Utilities.getContentType(filename)) {
                        case "text/html":
                            MessageHandler.sendHtmlResponseMsg(filePath, outToClient);
                            break;
                        case "image":
                            MessageHandler.sendImageResponseMsg(filePath, filename, outToClient);
                            break;
                    }
                }

                // If requested file doesn't exist ...
                else 
                {
                    // Holds the new url address
                    String newAddr = null;
                    
                    // Get the requested url to check if file exists
                    if (unparsedPath.charAt(unparsedPath.length() - 1) == '/')
                    {
                        unparsedPath = unparsedPath.substring(0, unparsedPath.length() -1);
                    }                   
                    
                    // Get the host
                    String host = (Utilities.findHostHeaderLine(msgLines).split(" "))[1].trim();
                    
                    // Create a full URL (with protocol, host and path)
                    String url = "http://" + host + unparsedPath;
                    
                    //url = Constants.BASE_URL + unparsedPath;
                    
                    //check if it has been permanently moved; if not, 
                    // show error message
                    if ((newAddr = Utilities.isFileMovedPermanently(filename, url)) != null)
                    {
                         // Send the moved permanently message
                        MessageHandler.sendMsg_301(filename, newAddr, outToClient);
                    }
                    
                    else
                    {
                        // NOT FOUND 404 error
                        MessageHandler.sendErrorMsg_404(filePath, filename, outToClient); 
                    }
                }
            }
            
            else
            {
                // Throw exception for empty request
                throw new BadRequestException();
            }
        }
        
        // Catch Socket Exception
        catch (SocketException e) 
        {
            // Print the error message
            System.out.println(e.getMessage());
        }
        
        // Catch IO Exception
        catch (IOException e) 
        {
            // Print the error message
            System.out.println(e.getMessage());
        }
        
        // Catch exception throw when request message has incorrect syntax
        catch (BadRequestException  e)
        {
            // Send the Bad Request Message
            MessageHandler.sendErrorMsg_400(filename, outToClient);
        }
        
        // Close the streams
        finally
        {
            if (inFromClient != null)
            {
                // Close input stream
                inFromClient.close();
            }
            
            if (outToClient != null)
            {
                // Close output stream
                outToClient.close(); 
            }
        }
    }    
}
