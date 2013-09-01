package org.myschool.dagucar.android.bridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Enumeration;

import org.myschool.dagucar.android.bridge.command.CommandUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class TcpBluetoothBridgeAndroid implements Runnable {
	
	private static final String DAGU_CAR = "DaguCar";
	//private String bserviceURL="btspp://001210240245:1;authenticate=false;encrypt=false;master=false"; //DaguCar Service

	private static final int port = 18080;
	//private static final String bindip = "127.0.0.1";
	private final CallbackInterface ui;
	private BluetoothSocket bsocket=null;
	private BluetoothDevice bdevice=null;
	private OutputStream os=null;
	
	public TcpBluetoothBridgeAndroid(CallbackInterface ui) {
		this.ui = ui;
	}

	@Override
	public void run() {
		ServerSocket server=null;
		Socket socket=null;
		InputStream is=null;
		
		try {
			
			//networkInfo();
			server = new ServerSocket(port);
			writeInfo("Opened server socket on "+server.getLocalSocketAddress() + " and port " + server.getLocalPort());
			socket=server.accept();
			is=socket.getInputStream();
			//handshake bytes
//			byte first=(byte)0x0F;
//			byte second=(byte)0xF0;

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
					writeInfo(CommandUtils.commandByteToString((byte)code) + " run for " +milliseconds + "ms");

					writeCodeToBluetoothStream((byte) code);
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
				if (bsocket!=null) bsocket.close();
				if (is!=null) is.close();
				if (socket!=null) socket.close();
				if (server!=null) server.close();
			} catch (Throwable e) {
				throw new IllegalStateException(e);
			}
		}

	}
	
	private void writeCodeToBluetoothStream(byte code) {
		try {
			os.write(code);
	        os.flush();
		}  catch (IOException e) {
			System.out.println("Could not send command " + code + "  to DaguCar. Error Message as follows:  \n\r" + e.getMessage());
			e.printStackTrace();
		}
        
	}
	
	private void writeInfo(String info) {
		System.out.println(info);
		ui.callback("\r\n"+info);
	}
	
	//Paired device seem not to work without re-discovery
	public boolean isDaguCarPaired() {
//		for (BluetoothDevice d:bAdapter.getBondedDevices() ) {
//			writeInfo("B-Device: " + d.getName() + " Mac:" + d.getAddress());
//			if ("DaguCar".equals(d.getName()) ) {
//				ParcelUuid[] uuids=d.getUuids();
//				for (ParcelUuid uuid:uuids) {
//					writeInfo("B-Device UUIDs: " + uuid.getUuid().toString());
//				}
//				if (bdevice == null) { //cache socket
//					bdevice = d;
//					bAdapter.cancelDiscovery();
//					bdevice.fetchUuidsWithSdp();
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e1) {throw new IllegalStateException("Was interrupted while accessing paired b-device.", e1);}
//					try {
//						uuids=bdevice.getUuids();
//						bsocket=bdevice.createInsecureRfcommSocketToServiceRecord(uuids[0].getUuid());
//						bsocket.connect();
//						os=bsocket.getOutputStream();
//					} catch (IOException e) {
//						throw new IllegalStateException("Paired-B-Device: Could not establish bluetooth socket to DaguCar", e);
//					}
//				}
//				return true;
//			}
//		}
		return false;
	}

	public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();	 
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            if (device==null) return;
	            ui.callback("Discovered B-Device: " +device.getName() + ", Mac: " + device.getAddress());
	            if (device.getUuids() !=null && device.getUuids().length>0)  ui.callback("Uuid: " + device.getUuids()[0].getUuid());
	            
	            if (DAGU_CAR.equals(device.getName())) {
	            	BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
	            	bdevice = device;
	            	try {
						bsocket = bdevice.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
						bsocket.connect();
						os=bsocket.getOutputStream();
					} catch (IOException e) {
						throw new IllegalStateException("Discovered-B-Device: Could not establish bluetooth socket to DaguCar", e);
					}
	            	
	            }
	            
	            
	        }	   	        
	    }

		
	};
	
	private TcpBluetoothBridgeAndroid getOuter() {
		return this;
	}
	
	//DEBUG Functions

	void networkInfo() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint);
    }

    void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
    	writeInfo("Display name: "+netint.getDisplayName());
    	writeInfo("Name: "+ netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
        	writeInfo("InetAddress: "+ inetAddress);
        }
     }

}
