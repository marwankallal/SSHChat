/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sshchat;

/**
 *
 * @author marwankallal
 */

// THIS IS CLIENT CODE
import java.util.*;
public class SSHChat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // ask for what user wants to do
        System.out.print("\n1: Pick a host and only run client"
                    + "\n2: Run a server"
                    + "\n3: Run a server and client simultaneously (usually used for private chats)"
                    + "\nPlease enter your selection: ");
        //get input
       Scanner s = new Scanner(System.in);
       int c = -1;
       //figure out what user wants to do
       while(c < 0)
       {
           //make sure user cant break it with strings
           try
           {
            c = s.nextInt();
           }
           catch(InputMismatchException e)
           {
               System.out.print("\nNot a valid input. Please try again: ");
           }
       }
       int port = -1;
       String host;
       //implement what user wants
        switch(c){
            case 1:
                System.out.print("\nPlease specify the host: ");
                host = s.next();
                System.out.print("\nPlease specify the port: ");
                try
                {
                    port = s.nextInt();
                }
                catch(InputMismatchException e)
                {
                    System.out.print("\nThat is not a valid input. Exiting...");
                    System.exit(1);
                }
                Client client = new Client(host, port);
                break;
            case 2:
                //make server
                System.out.print("\nPlease specify the port: ");
                try
                {
                    port = s.nextInt();
                }
                catch(InputMismatchException e)
                {
                    System.out.print("\nThat is not a valid input. Exiting...");
                    System.exit(1);
                }
                Server server = new Server(port);
                break;
            case 3:
                //make both
                System.out.print("\nPlease specify the port: ");
                try
                {
                    port = s.nextInt();
                }
                catch(InputMismatchException e)
                {
                    System.out.print("\nThat is not a valid input. Exiting...");
                    System.exit(1);
                }
                Server sserver = new Server(port);
                Client sclient = new Client("127.0.0.1", port);
                break;
            default:
                //do nothing
                System.out.print("\nThat is not a valid input. Exiting...");
                System.exit(1);
                break;
        }
       


        
        
        // GUI STUFF
        
    }
}
