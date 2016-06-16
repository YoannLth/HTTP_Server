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
 * @author Yoann LATHUILIERE
 */
public class HTTPServer {

    // Informations du serveur HTTP
    private static final int HTTP_DEFAULT_PORT = 1026; // Port utilisé
    private ServerSocket serverSocket = null; // Serveur socket

    /**
     * Fonction qui initilise le serveur en créant un serveur socket sur le port définie et attend les connexions
     */
    public void initializeServer() {
        try {
            serverSocket = new ServerSocket(HTTP_DEFAULT_PORT); // Création du serveur socket sur le port définie
            System.out.println("Listening for connection on port 1026 ....");
            // Boucle infinie qui attend les connexions
            while (true) {
                Socket replySocket;
                try
                {
                    replySocket = serverSocket.accept(); // Accepte la connexion
                    ThreadCommunication tc = new ThreadCommunication(replySocket); // Crée un nouveau ThreadCommunication (thread qui gère les communications avec le client)
                    tc.start(); // Démarrage du thread
                } catch (IOException ex)
                {
                    Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Création du socket sur le port 1026 impossible car le port est occupé");
        }

    }

}
