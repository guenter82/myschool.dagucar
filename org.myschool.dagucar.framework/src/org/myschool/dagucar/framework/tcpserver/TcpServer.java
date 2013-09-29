package org.myschool.dagucar.framework.tcpserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.myschool.dagucar.framework.bluetooth.DaguCar;
import org.myschool.dagucar.framework.command.CommandUtils;
import org.myschool.dagucar.framework.program.ServerContext;

public class TcpServer implements Runnable {

	private static final int port = 8080;
	//private static final String bindip = "127.0.0.1";

	@Override
	public void run() {
		ServerSocket server=null;
		Socket socket=null;
		InputStream is=null;
		try {
			server = new ServerSocket(port);
			socket=server.accept();
			is=socket.getInputStream();
			//handshake bytes
			
			for (int i=0; i<10000; i++) {
				byte first=(byte) is.read();
				if (first != ServerContext.firstHandshakeByte) continue;
				byte second=(byte) is.read();
				if (second != ServerContext.secondHandshakeByte) continue;
				byte serviceByte = (byte) is.read();
				
				//sending a command to dagu car
				if (serviceByte == ServerContext.handleCommandServiceByte) {
					int daguCarNumber = is.read();
					DaguCar daguCar = ServerContext.dagucars.get(daguCarNumber);
					if (daguCar == null) {
						System.out.println("DaguCar with number " + daguCar + " could not be found! Command will be ignored.");
					}
					int code = is.read(); //blocking until data is available
					byte millisecondsBytes[]=new byte[4];
					
					if (code != -1) {
						int nextByte;
						if ( (nextByte = is.read()) != -1 ) millisecondsBytes[0] = (byte)nextByte; else throw new IllegalStateException("Could not read reapting time because stream is closed.");
						if ( (nextByte = is.read()) != -1 ) millisecondsBytes[1] = (byte)nextByte; else throw new IllegalStateException("Could not read reapting time because stream is closed.");
						if ( (nextByte = is.read()) != -1 ) millisecondsBytes[2] = (byte)nextByte; else throw new IllegalStateException("Could not read reapting time because stream is closed.");
						if ( (nextByte = is.read()) != -1 ) millisecondsBytes[3] = (byte)nextByte; else throw new IllegalStateException("Could not read reapting time because stream is closed.");
						
						//put this to manager (queue and handler thread)
						ByteBuffer buffer=ByteBuffer.allocate(4).put(millisecondsBytes);
						buffer.position(0);
						int milliseconds=buffer.getInt();
						System.out.println(CommandUtils.commandByteToString((byte)code) + " run for " +milliseconds + "ms");
						daguCar.queue((byte)code, milliseconds);
					}
				}
			}

		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				if (is!=null) is.close();
				if (socket!=null) socket.close();
				if (server!=null) server.close();
			} catch (Throwable e) {
				throw new IllegalStateException(e);
			} finally {
				ServerContext.cleanUpDaguCars();
			}
		}

	}



}
