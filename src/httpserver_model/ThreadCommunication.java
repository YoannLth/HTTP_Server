/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver_model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yoann LATHUILIERE
 */
public class ThreadCommunication extends Thread{
    // Création d'un socket pour la réponse
    Socket replySocket = null;

    /**
     * Constructeur du thread
     */
    public ThreadCommunication(Socket s) {
        replySocket = s;    
    }

    /**
     * Fonction run du thread, qui est chargé de récuperer la requête du serveur et de l'executer
     */
    @Override
    public void run() {
        String request;
        try {
            replySocket.setKeepAlive(true);
        } catch (SocketException ex) {
            Logger.getLogger(ThreadCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            try {
                
                InputStream is = replySocket.getInputStream(); // Récupère la requete du client
                InputStreamReader r = new InputStreamReader(is);  // Création d'un buffer à partir du la requête
                BufferedReader br = new BufferedReader(r); // Création d'un buffer à partir du la requête
                request = br.readLine(); // Lit la première ligne de la requête
                analyseRequest(request); // Vérifie si la requête est valide et traite la requête si elle l'est
            } catch (IOException ex) {
                Logger.getLogger(ThreadCommunication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Fonction qui vérifie si une requête est valide (la requête est bien un 'GET') et execute la requête ensuite
     * @param s
     * @return 0 si tout s'est bien passé, 
     *         -1 car toutes requetes autre que GET ne sont pas prises en charge.
     *         -2 si le fichier demandé n'a pas été trouvé
     *         -3 si le serveur a rencontré un problème.
     */
    public int analyseRequest(String s){
        String[] parts = s.split("\\ "); // Sépare la requete en 'mots' pour pouvoir l'analiser (on considère un mot par une suite de caractères finie par un espace ' ')
        String httpRequestType = parts[0];    // Premier mot de la requete, soit le type (GET, POST...)
        String httpRequest = parts[1]; // Deuxieme mot de la requête, soit le fichié demandé
        //On affiche ce que demande le client.
        System.out.println("'" + this.replySocket.getInetAddress().getHostAddress() + "'" + " demande " + "'" + s + "'");
        //On met le premier mot en majuscule (convention).
        httpRequestType.toUpperCase();

        // Seul la fonction GET est supporté actuellement
        switch (httpRequestType) {
            case "GET":  
                return treatGetRequest(httpRequest);
            default: 
                return -1;
        }
    }
    
    /**
     * Fonction qui traite la requête 'GET' et renvoi le résultat attendu
     * @param request
     * @return 0 si tout s'est bien passé, 
     *         -2 si le fichier demandé n'a pas été trouvé
     *         -3 si le serveur a rencontré un problème.
     */
    public int treatGetRequest(String request){
        //Content-Type à renvoyer
        String contentType = "Content-Type: ";
        //Content-Length à renvoyer
        String contentLength = "Content-Length: ";
        try{
            //Fichier à ouvrir
            FileInputStream file; 
            //On ouvre le fichier, s'il n'existe pas on catch l'exception
            file = new FileInputStream(request);
            //On sauvegarde la taille du fichier à envoyer
            int fileSize = (int) file.getChannel().size();
            contentLength += fileSize;//Ajout de la taille du fichier
            //On split en fonction des . pour obtenir l'extension
            String[] parts = request.split("\\."); 
            String ext = parts[1].toLowerCase();
            
            //On crée un tableau de bytes pour envoyer le message
            byte[] fileContent = new byte[fileSize];
            //On lit le fichier
            file.read(fileContent,0,fileSize);
            //On prend la date d'aujourd'hui
            Date today = new Date();
            //On récupère l'adresse
            InetAddress ip = InetAddress.getLocalHost();
            //On récupère le nom de l'hote
            String hostname = ip.getHostName();
            //On construit la réponse HTTP 200.
            String httpResponse = "\r\n" + "HTTP/1.1 200 OK\r\n" + "Date: " + today + "\r\n" + "Server: " + hostname + "\r\n" + contentLength + "\r\n";
            
            switch(ext){
                case "txt":
                    contentType += "text/txt";
                    
                    break;
                case "html":
                    contentType += "text/html";
                    break;
                case "jpg":
                    contentType += "image/jpg";
                    break;
                case "png":
                    contentType += "image/png";                 
                    break;
                default :
                    break;
            } 
            httpResponse += contentType + "\r\n\r\n";
            file.close();
            byte[] message = new byte[httpResponse.getBytes().length+fileContent.length]; 
            System.arraycopy(httpResponse.getBytes(), 0, message, 0, httpResponse.getBytes().length);
            System.arraycopy(fileContent, 0, message, httpResponse.getBytes().length, fileContent.length);
            replySocket.getOutputStream().write(message);
            return 0;
        } catch (FileNotFoundException ex)
        {
            send404Code();
            return -2;
        } catch (IOException ex)
        {
            return -3;
        }
        
    }
    
    /**
     * Fonction envoyant une erreur 404.
     */
    public void send404Code()
    {
        try
        {
            String contentLength = "Content-Length: ";
            //Fichier à ouvrir
            FileInputStream file; 
            //On ouvre le fichier 404.html
            file = new FileInputStream("404.html");
            //On sauvegarde la taille du fichier à envoyer
            int fileSize = (int) file.getChannel().size();
            contentLength += fileSize;//Ajout de la taille du fichier;
            //On crée un tableau de bytes pour envoyer le message
            byte[] fileContent = new byte[fileSize];
            //On lit le fichier
            file.read(fileContent,0,fileSize);
            //On prend la date d'aujourd'hui
            Date today = new Date();
            //On récupère l'adresse
            InetAddress ip = InetAddress.getLocalHost();
            //On récupère le nom de l'hote
            String hostname = ip.getHostName();
            //On construit la réponse HTTP 404.
            String httpResponse = "\r\n" + "HTTP/1.1 404 NOT FOUND\r\n"+ "Date: " + today + "\r\n" + "Server: " + hostname + "\r\n" + contentLength + "\r\n";
            httpResponse += "Content-Type: text/html" + "\r\n\r\n";
            file.close();
            byte[] message = new byte[httpResponse.getBytes().length+fileContent.length]; 
            System.arraycopy(httpResponse.getBytes(), 0, message, 0, httpResponse.getBytes().length);
            System.arraycopy(fileContent, 0, message, httpResponse.getBytes().length, fileContent.length);
            replySocket.getOutputStream().write(message);
        } catch (UnknownHostException ex)
        {
            Logger.getLogger(ThreadCommunication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(ThreadCommunication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(ThreadCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
