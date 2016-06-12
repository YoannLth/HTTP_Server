/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver_model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yoann
 */
public class HTTPServer {

    private static final int HTTP_DEFAULT_PORT = 1026;
    private ServerSocket serverSocket = null;

    public void initializeServer() throws IOException {
        try {
            serverSocket = new ServerSocket(HTTP_DEFAULT_PORT);
        } catch (IOException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Listening for connection on port 1026 ....");
        while (true) {
            Socket replySocket = serverSocket.accept();            
            ThreadCommunication tc = new ThreadCommunication(replySocket);
            tc.start();
        }

    }

}
