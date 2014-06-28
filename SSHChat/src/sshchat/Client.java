package sshchat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
import javax.net.ssl.*;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.security.*;
import java.util.*;
public class Client implements Runnable
{
    int port;
    String host;
    SSLSocket socket;
    SSLSocketFactory factory;
    BufferedReader in;
    DataOutputStream out;
    ClientThread cthread;
    Thread thread;
    
    public Client(String name, int sport)
    {
        System.out.println("Connecting...");
        //do all the ssh key exchange and connection
        try
        {
            
            char[] passphrase = "passphrase".toCharArray();

            String keypath = "key";
            //String trustpath = "trust.jks";
            // First initialize the key and trust material.
            KeyStore ksKeys = KeyStore.getInstance("JKS");
            ksKeys.load(new FileInputStream(new File(keypath)), passphrase);
            KeyStore ksTrust = KeyStore.getInstance("JKS");
            ksTrust.load(new FileInputStream(new File(keypath)), passphrase);

            // KeyManager's decide which key material to use.
            KeyManagerFactory kmf =
                KeyManagerFactory.getInstance("SunX509");
            kmf.init(ksKeys, passphrase);

            // TrustManager's decide whether to allow connections.
            TrustManagerFactory tmf =
                TrustManagerFactory.getInstance("SunX509");
            tmf.init(ksTrust);

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(
                kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            //create factory and socket to connect to server with
            factory = ctx.getSocketFactory();
            socket = (SSLSocket) factory.createSocket(name,sport);
            System.out.println("Starting...");
            start();
        }
        catch(UnknownHostException e)
        {
            System.out.println("Host Doesnt exist");
        }
        catch(IOException e)
        {
            System.out.println("IO Error" + e);
        }
        catch(KeyStoreException e)
        {
            System.err.println("Keystore Error");
        }
        catch(NoSuchAlgorithmException e)
        {
            System.out.println("Key Error: Nonexistent Algorithm");
        }
        catch(KeyManagementException e)
        {
            System.out.println("Key Management Error");
        }
        catch(CertificateException e)
        {
            System.out.println("Certificate Error");
        }
        catch(UnrecoverableKeyException e)
        {
            System.out.println("Key Error: Could not recover key");
        }
        
    }
    
    public void run()
    {
        //continueously read for user input to write up the the server
        while(thread != null)
        {
            System.out.print("> ");
            try
            {
                out.writeUTF(in.readLine());
                out.flush();
            }
            catch(IOException e)
            {
                System.out.println("Could not send. Quitting...");
                //stop();
                System.exit(1);
            }
        }
    }
    
    public void start() throws IOException
    {
        //implements read and write streams (read from user and write to server)
        in = new BufferedReader(new InputStreamReader(System.in));
        out = new DataOutputStream(socket.getOutputStream());
        if(thread == null)
        {
            // make new threads to run client on
            cthread = new ClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }
    
    public void stop()
    {
        if(thread != null)
        {
            try
            {
                //kill this thread
                thread.join();
                thread = null;
            }
            catch(InterruptedException e)
            {
                System.out.print("Error on closing...");
            }
            
            try
            {
                if(in != null && out != null && socket != null)
                {
                    //close all streams
                    out.close();
                    in.close();
                    socket.close();
                }
            }
            catch(IOException e)
            {
                System.err.println("Error on closing...");
            }
            //make sure everything is closed
            cthread.close();
            try
            {
                //quit
                cthread.join();
            }
            catch(InterruptedException e)
            {
                System.out.println("Error on closing...");
            }
        }
    }
}
