package org.myschool.dagucar.framework.test.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.myschool.dagucar.framework.command.CommandUtils;
import org.myschool.dagucar.framework.remote.ConnectionProviderBluetooth;
import org.myschool.dagucar.framework.remote.RemoteControl;

public class OldTcpServer implements Runnable {

	private static final int port = 8080;
	//private static final String bindip = "127.0.0.1";

	@Override
	public void run() {
		ServerSocket server=null;
		Socket socket=null;
		InputStream is=null;
		ConnectionProviderBluetooth bluetooth=null;
		OutputStream os=null;
		try {
			server = new ServerSocket(port);
			socket=server.accept();
			is=socket.getInputStream();
			//handshake bytes
//			byte first=(byte)0x0F;
//			byte second=(byte)0xF0;
			bluetooth = new ConnectionProviderBluetooth();
			os=bluetooth.openConnection("");
			for (int i=0; i<10000; i++) {
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
					writeCodeToBluetoothStream(os, (byte) code);
					Thread.sleep(milliseconds);
					//end
					
				}
			}

		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				if (os!=null) os.close();
				if (bluetooth!=null) bluetooth.closeConnection(os);
				if (is!=null) is.close();
				if (socket!=null) socket.close();
				if (server!=null) server.close();
			} catch (Throwable e) {
				throw new IllegalStateException(e);
			}
		}

	}
	
	private void writeCodeToBluetoothStream(OutputStream os, byte code) {
		try {
			os.write(code);
	        os.flush();
		}  catch (IOException e) {
			System.out.println("Could not send command " + CommandUtils.commandByteToString(code) + "  to DaguCar. Error Message as follows:  \n\r" + e.getMessage());
			e.printStackTrace();
		}
        
	}


}
