/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sshchat;

/**
 *
 * @author marwankallal
 */
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
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
public class ClientThread extends Thread
{
    SSLSocketFactory factory;
    SSLSocket socket;
    Client client;
    DataInputStream t_in;
    
    
    public ClientThread(Client c, SSLSocket s)
    {
        client = c;
        socket = s;
        open();
        this.start();
    }
    
    public void open()
    {
        //open up all the streams
        try
        {
            t_in = new DataInputStream(socket.getInputStream());
            System.out.println("Connection Opened...");
        }
        catch(IOException e)
        {
            System.out.println("Read Error");
            client.stop();
        }
    }
    
    public void close()
    {
        //close out all the streams to ensure theres no memory leaks
        try
        {
            if(t_in != null)
            {
                t_in.close();
            }
        }
        catch(IOException e)
        {
            System.out.println("Error on closing");
        }
    }
    
    public void run()
    {
        while(true)
        {
            try
            {
                //print out all the things sent to this client, as well as the line prompt ">"
                System.out.println(t_in.readUTF());
                System.out.print("> ");
            }
            catch(IOException e)
            {
                System.out.println("Read Error:" + e);
                client.stop();
            }
        }
    }
}
