/**
 *
 * @author Gaurab R. Gautam
 */


// Package name
package MultiThreadTCPServer;

// Imported libraries
import java.io.IOException;
import java.net.*;

/**
 * Name:        MultiThreadTCPServer
 * Type:        Class
 * Description: Main class that is an entry point of the program. Accepts client
 *              socket connection, and spurns a new thread for each client
 */
public class MultiThreadTCPServer 
{
    // ServerSocket variable used to welcome client sockets (handshaking)
    static ServerSocket welcomeSocket = null;
    
    // Socket that connects to the client to transfer data
    private static Socket connectionSocket = null;
        
    /**
     * Name       : main
     * Input      : Command line arguments into array of string
     * Output     : none
     * Description: Entry point of the application
     * @param argv
     * @throws java.lang.Exception
     */
    public static void main(String argv[]) throws Exception
    {
        try
        {
            // Create wecome socket to allow clients to initiate TCP connection
            welcomeSocket = new ServerSocket(Constants.PORT);
            
            // Keep running to accept connections
            while(true) 
            {
                // Start listening to connections
                connectTCPClient();
            }
        }
        
        catch (IOException e)
        {
            // Print error message and exit application
            System.out.println("Couldn't create socket!");
        }
    }
    
    /**
     * Name       : connectTCPClient
     * Input      : none
     * Output     : none
     * Description: Establishes the unique connection with a client for
     *              communication through socket
     */
    public static void connectTCPClient() throws Exception
    {
        try 
        { 
            // Accept and establish a dedicated connection to each client
            connectionSocket = welcomeSocket.accept();
            
            // Create an instance of MultithreadTCPClientConnection class that
            // is run in a unique thread
            MultithreadTCPClientConnection clientConn = 
                        new MultithreadTCPClientConnection(connectionSocket);
            
            // Create and start a new thread to run each client connection
            Thread clientConnThread = new Thread(clientConn);
            clientConnThread.start();
        } 
        
        catch (IOException e) 
        {
             // Print error message and exit application
            System.out.println("Couldn't accept connection!");
        }
    }
}
