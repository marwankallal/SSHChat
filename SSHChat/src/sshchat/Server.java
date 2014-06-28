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
import javax.net.ssl.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
public class Server implements Runnable{
    ServerThread clist[] = new ServerThread[100];
    SSLServerSocketFactory sfactory;
    SSLServerSocket server;
    Thread thread;
    int pop;
    SSLContext ctx;
    
    public Server(int p)
    {
        // again more of that ssl connection stuff
        try
        {
			// TODO change passphrase to random assortment (ASCII: 65-90, 97-122)
			//send passphrase and key
            char[] passphrase = "passphrase".toCharArray();
			
            String keypath = "key";
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

            //get the contexts from which to make factories to make sockets
            ctx = SSLContext.getInstance("TLS");
            ctx.init(
                kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            sfactory = ctx.getServerSocketFactory();
            server = (SSLServerSocket) sfactory.createServerSocket(p);
            start();
        }
        catch(IOException e)
        {
            System.out.println("Server could not connect to port");
        }
        catch(KeyStoreException e)
        {
            System.err.println("Server Keystore Error");
        }
        catch(NoSuchAlgorithmException e)
        {
            System.out.println("Server Key Error: Nonexistent Algorithm");
        }
        catch(KeyManagementException e)
        {
            System.out.println("Server Key Management Error");
        }
        catch(CertificateException e)
        {
            System.out.println("Server Certificate Error");
        }
        catch(UnrecoverableKeyException e)
        {
            System.out.println("Server Key Error: Could not recover key");
        }
    }
    
    public void run()
    {
        while(thread != null)
        {
            try
            {  
                //keep listening for incoming connections and accept them (assuming ssl works out)
                addThread((SSLSocket) server.accept());
                System.out.println("New Connection Recieved");
            }
            catch(IOException e)
            {
                System.out.println("Could not connect to port: " + e);
               
            }
        }
    }
    
    //find the id of numbered client
    private int getClient(int n)
    {
        for(int i = 0; i < pop; i++)
        {
            if(clist[i].getID() == n)
            {
                return i;
            }
        }
        return -1;
    }
    
    //gets rid of client from the server
    public synchronized void remove(int n)
    {
        int pos = getClient(n);
        if(pos >= 0)
        {
            //find the right thread
            ServerThread quitthread = clist[pos];
            System.out.println("Removing client " + n);
            pop--;
            try
            {
                //kill it
                quitthread.close();
            }
            catch(IOException e)
            {
                System.out.println("Error on closing...");
            }
            //stop();
            clist[pos] = null;
        }
    }
    
    public void manage(String str, String hand)
    {
        //make @USR:: actually private (to all except server)
        String[] parts = str.split("::");
        if(parts.length > 1)
        {
            char[] head = parts[0].toCharArray();
            if(head[0]=='@')
            {
                String targethndl = parts[0].substring(1, parts[0].length());
                //find the right serverthread to send it through
                for (ServerThread serv : clist)
                {
                    if(serv != null && serv.handle.equals(targethndl))
                    {
                        //send it nicely formatted
                        serv.send("(PRIVATE)" + hand + ": " + parts[1]);
                    }
                }
                return;
            }
        }
        //send everything else normally
        for (ServerThread serv : clist)
        {
            //send to everything except itself
            if(serv != null && !hand.equals(serv.handle))
            {
                serv.send(hand + ": " + str);
            }
        }
    }
    
    // actually adding users to server
    private void addThread(SSLSocket socket)
    {
        if(pop < clist.length)
        {
            for(ServerThread srv : clist)
            {
                if(srv == null)
                {
                    //make a new thread to run data through to new client
                    srv = new ServerThread(this, socket);
                    
                    try
                    {
                        //start up the thread
                        srv.open();
                        start();
                        
                        //set everything up here (increment pop count, keep track of thread)
                        clist[pop] = srv;
                        pop++;
                        break;
                    }
                    catch(IOException e)
                    {
                        System.out.println("Cannot open new thread");
                    }
                    
                    
                }
                
            }
            System.out.println("New Connection: " + socket);

        }
        else
        {
            System.out.println("Sorry, server full at " + pop);
        }
    }
    
    public void start()
    {  
        if (thread == null)
        {  
            //make a new thread
            thread = new Thread(this); 
            thread.start();
        }
    }
    public void stop()
    {
        if (thread != null)
        {  
            try
            {
                //kill thread
                thread.join();
            }
            catch (InterruptedException e)
            {
                System.out.println("Error on close");
            }
            thread = null;
        }
    }
}
