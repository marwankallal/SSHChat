package sshchat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marwankallal
 */
 
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLContext;
import java.io.Serializable;

public class ConnectionData implements Serializable
{
	private char[] pass;
	private SSLSocket socket;
	private SSLContext context;

	public ConnectionData(char[] p, SSLSocket s, SSLContext c)
	{
		pass = p;
		socket = s;
		context = c;
	}

	//return passphrase
	public char[] getPass()
	{
		return pass;
	}

	//return socket
	public SSLSocket getSocket()
	{
		return socket;
	}
	
	//return context
	public SSLContext getContext()
	{
		return context;
	}
}