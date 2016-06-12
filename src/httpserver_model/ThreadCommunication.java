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
 * @author Yoann
 */
public class ThreadCommunication extends Thread{

    Socket replySocket = null;

    public ThreadCommunication(Socket s) {
        replySocket = s;
    }

    @Override
    public void run() {
        String request = "";
        while (true) {
            try {
                int i;
                
                InputStream is = replySocket.getInputStream();
                InputStreamReader r = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(r);
                request = br.readLine();
                
                boolean requestValid = requestIsValid(request);
            } catch (IOException ex) {
                Logger.getLogger(ThreadCommunication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean requestIsValid(String s){
        try
        {
            String[] parts = s.split("\\ "); // String array, each element is text between dots
            String httpRequestType = parts[0];    // Text before the first dot
            String httpRequest = parts[1];
            System.out.println(httpRequestType);
            httpRequestType.toUpperCase();

            switch (httpRequestType) {
                case "GET":  
                    System.out.println("GET REQUEST");
                    treatGetRequest(httpRequest);
                    break;
                default: 
                    System.out.println("INVALID REQUEST");
                    break;
            }
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }
    
    public void treatGetRequest(String request){
        System.out.println(request);
        if(request.charAt(0) == '/'){
            request = request.substring(1);
        }
        String contentType = "Content-Type: ";
        String contentLength = "Content-Length: ";
        try{
            FileInputStream file; 
            //file = new FileInputStream(getClass().getResource("/serverFiles/" + request).toString());
            //file = new FileInputStream(getClass().getResource("/serverFiles/test.txt").toExternalForm());
            //file = new FileInputStream("httpserver_serverFiles/test.txt");
            file = new FileInputStream(request);
            
            int fileSize = (int) file.getChannel().size();
            contentLength += fileSize;
            String[] parts = request.split("\\."); 
            String ext = parts[1].toLowerCase();
            System.out.println(ext);
            
            byte[] fileContent = new byte[fileSize];
            file.read(fileContent,0,fileSize);
            System.out.println(fileContent.length);
            
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
