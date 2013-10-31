package org.myschool.dagucar.framework.remote;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.myschool.dagucar.framework.command.DirectionCommand;
import org.myschool.dagucar.framework.command.SpeedCommand;
import org.myschool.dagucar.framework.program.ServerContext;

public class ConnectionProviderTcpClient implements ConnectionProvider {

	private final String serverip;
	private final int port;
	private Socket socket;
	private OutputStream os;


	public ConnectionProviderTcpClient() {
		this.port = ServerContext.context.port;
		this.serverip = ServerContext.context.ip;
	}


	@Override
	public void openConnection() throws IOException {
		System.out.println("Used IP / Port: " + this.serverip + " / " + this.port );
		if (this.serverip==null || this.port <= 0) {
			throw new IllegalStateException("Service IP and port must not be null when cmd is sent!");
		}
		/*
		 * Open the connection to the Tcp server
		 */
		this.socket = new Socket(this.serverip, this.port);
		/*
		 * Sends data to remote device
		 */
		this.os = this.socket.getOutputStream();
	}

	@Override
	public void closeConnection() throws IOException {
		if (this.os!=null) {
			this.os.close();
		}
		if (this.socket!=null) {
			this.socket.close();
		}
		this.socket = null;
		System.out.println("Tcp client connection to DaguCar was safely closed");
	}


	@Override
	public void writeAndFlush(int dagucarnumber, DirectionCommand direction, SpeedCommand speed, int milliseconds) throws IOException {
		if (this.os==null || this.socket==null || !this.socket.isConnected()) {
			this.closeConnection();
			this.openConnection();
		}

		ByteBuffer buffer = ByteBuffer.allocate(9);
		buffer.put(ServerContext.firstHandshakeByte);
		buffer.put(ServerContext.secondHandshakeByte);
		buffer.put(ServerContext.handleCommandServiceByte);
		buffer.put((byte)dagucarnumber);
		buffer.put((byte)(direction.code + speed.code));
		byte[] bytes =buffer.putInt(milliseconds).array();
		this.os.write(bytes);
		this.os.flush();
	}

}
