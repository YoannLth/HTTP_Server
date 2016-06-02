/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver_model;

import java.io.IOException;
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
        Date today = new Date();
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + today + "\r\n\r\n";
        try {
            replySocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(ThreadCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            try {
                System.out.println(replySocket.getInputStream().read());
            } catch (IOException ex) {
                Logger.getLogger(ThreadCommunication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
