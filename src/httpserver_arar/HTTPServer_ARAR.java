/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver_arar;

import httpserver_model.HTTPServer;
import java.io.IOException;

/**
 *
 * @author Yoann LATHUILIERE
 */
public class HTTPServer_ARAR {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        HTTPServer httpServer = new HTTPServer();
        httpServer.initializeServer();
    }
    
}
