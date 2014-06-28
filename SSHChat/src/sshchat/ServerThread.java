/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sshchat;

import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.io.*;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
/**
 *
 * @author marwankallal
 */
public class ServerThread extends Thread{
    Server server;
    SSLSocket socket;
    int ID = -1;
    DataInputStream in;
    DataOutputStream out;
    Thread thread;
    String handle;
    
    private final int MAX_GUEST = 10000;
    
    public ServerThread(Server serv, SSLSocket sslsock)
    {
        super();
        server = serv;
        socket = sslsock;
        ID = socket.getPort();
        handle = "USR " + ID;
    }
    public void send (String str)
    {
        try
        {
            //write info out to the stream
            if(str != "" && str != null)
            {
                out.writeUTF(str);
                out.flush();
            }
        }
        catch(IOException e)
        {
            System.out.println("Cant write");
            server.remove(ID);
            server.stop();
        }
    }
    public int getID()
    {
        //just return the id (unique identifier)
        return ID;
    }
    
    public void run()
    {
        //tells it to send to manager of the server to send out whatever is read and the users handle
        while (true)
        {  
            try
            {  
                server.manage(in.readUTF(), handle);
            }
            catch(IOException e)
            {  
                System.out.println("Could not read from " + handle + ": terminating thread");
                //remove thread if broken
                server.remove(ID);
            }

        }
    }
    
    public void open() throws IOException
    {
        //open up all the streams
        in = new DataInputStream((socket.getInputStream()));
        out = new DataOutputStream((socket.getOutputStream()));
        if (thread == null)
        {  
            //make the new thread
            thread = new Thread(this); 
            thread.start();
        }
    }
    

    //close up all the streams to make sure no leaks occur
    public void close() throws IOException
    {
        if(socket != null && in != null && out != null)
        {
            socket.close();
            in.close();
            out.close();
            try
            {
                thread.join();
            }
            catch(InterruptedException e)
            {
                System.out.println("is broke");
            }
        }
    }
        
}
