package org.myschool.dagucar.framework.remote;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionProviderTcpClient implements ConnectionProvider {

	private String serverip;
	private final int port;
	private Socket socket;


	public ConnectionProviderTcpClient(int port) {
		//serverip="10.63.208.218"
		//port = 8080
		this.port = port;
	}


	@Override
	public OutputStream openConnection(Object connectionParameter) throws IOException {
		serverip = String.valueOf(connectionParameter);
		System.out.println("Used IP / Port: " + serverip + " / " + port );
        if (serverip==null || port <= 0) {
            throw new IllegalStateException("Service IP and port must not be null when cmd is sent!");
        }
	    /*
        * Open the connection to the Tcp server
        */
        this.socket = new Socket(serverip, port);
        /*
        * Sends data to remote device
        */
		OutputStream os = socket.getOutputStream();
		return os;
	}

	@Override
	public void closeConnection(OutputStream os) throws IOException {
		if (os!=null) os.close();
		if (socket!=null) socket.close();
		socket = null;
		System.out.println("Tcp client connection to DaguCar was safely closed");
	}

}
