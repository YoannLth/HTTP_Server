/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver_model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
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
     */
    public void analyseRequest(String s){
        try
        {
            String[] parts = s.split("\\ "); // Sépare la requete en 'mots' pour pouvoir l'analiser (on considère un mot par une suite de caractères finie par un espace ' ')
            String httpRequestType = parts[0];    // Premier mot de la requete, soit le type (GET, POST...)
            String httpRequest = parts[1]; // Deuxieme mot de la requête, soit le fichié demandé
            System.out.println("'" + this.replySocket.getInetAddress().getHostAddress() + "'" + " demande " + "'" + s + "'");
            httpRequestType.toUpperCase();

            // Seul la fonction GET est supporté actuellement
            switch (httpRequestType) {
                case "GET":  
                    treatGetRequest(httpRequest);
                    break;
                default: 
                    System.out.println("INVALID REQUEST");
                    break;
            }
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }
    
    /**
     * Fonction qui traite la requête 'GET' et renvoi le résultat attendu
     */
    public void treatGetRequest(String request){
        if(request.charAt(0) == '/'){
            request = request.substring(1);
        }
        String contentType = "Content-Type: ";
        String contentLength = "Content-Length: ";
        try{
            FileInputStream file; 
            file = new FileInputStream(request);
            
            int fileSize = (int) file.getChannel().size();
            contentLength += fileSize;
            String[] parts = request.split("\\."); 
            String ext = parts[1].toLowerCase();
            
            byte[] fileContent = new byte[fileSize];
            file.read(fileContent,0,fileSize);
            
            Date today = new Date();
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            
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
        } catch (Exception ex) {
            treatGetRequest("404.html");
        }
       
    }

}
