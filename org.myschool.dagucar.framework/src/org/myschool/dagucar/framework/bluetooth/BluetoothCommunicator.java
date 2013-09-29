package org.myschool.dagucar.framework.bluetooth;

import java.io.IOException;
import java.io.OutputStream;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class BluetoothCommunicator {
	private StreamConnection con;
	private OutputStream os;
	private final DaguCar daguCar;
	private final String serviceURL;
	
	public BluetoothCommunicator(DaguCar daguCar) {
		this.daguCar = daguCar;
		this.serviceURL = daguCar.getServiceUrl();
		
	}

	private OutputStream openConnection() throws IOException {

		System.out.println("Used Bluetooth Stack: " + LocalDevice.getProperty("bluecove.stack"));
        if (serviceURL==null) {
            throw new IllegalStateException("Service URL must not be null when cmd is sent!");
        }
	    /*
        * Open the connection to the bluetooth server
        */
		this.con =(StreamConnection)Connector.open(serviceURL.toString());
        /*
        * Sends data to remote device
        */
		this.os = con.openOutputStream();
		return os;
	}

	private void closeConnection() throws IOException {
		if (os!=null) os.close();
		if (con!=null) con.close();
		os = null;
		con = null;
		System.out.println("Bluetooth connection to DaguCar was safely closed");
	}

	public void send(byte code) throws IOException {
		try {
			if (this.os==null) this.openConnection(); 
			this.os.write(code);
			this.os.flush();
		} catch (Throwable e) {
			cleanup();
			//retry
			this.openConnection();
			this.os.write(code);
			this.os.flush();
		}
	}

	public void cleanup() {
		try {//cleanup
			this.closeConnection();
		} catch (Throwable e) {
		}
	}

	public boolean check() {
		try {//cleanup
			this.openConnection();
			return true;
		} catch (Throwable e) {
			System.out.println(daguCar.toString() + " currently not online: " + e.getMessage());
			return false;
		}
	}


}
