package org.myschool.dagucar.plugin.remote;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class BluetoothCommunicator {

	private StreamConnection con;
	private OutputStream os;
	private final DaguCarRemote daguCarRemote;
	private final String serviceURL;

	public BluetoothCommunicator(DaguCarRemote daguCar) {
		this.daguCarRemote = daguCar;
		this.serviceURL = daguCar.getServiceUrl();

	}

	private OutputStream openConnection() throws IOException {

		//System.out.println("Used Bluetooth Stack: " + LocalDevice.getProperty("bluecove.stack"));
		if (this.serviceURL==null) {
			throw new IllegalStateException("Service URL must not be null when cmd is sent!");
		}
		/*
		 * Open the connection to the bluetooth server
		 */
		this.con =(StreamConnection)Connector.open(this.serviceURL.toString());
		/*
		 * Sends data to remote device
		 */
		this.os = this.con.openOutputStream();
		System.out.println("Bluetooth connection to " +this.daguCarRemote+ " established.");
		return this.os;
	}

	private void closeConnection() throws IOException {
		if (this.os!=null) {
			this.os.close();
		}
		if (this.con!=null) {
			this.con.close();
		}
		this.os = null;
		this.con = null;
		System.out.println("Bluetooth connection to " +this.daguCarRemote+ " was closed.");
	}

	public void send(byte code) throws IOException {
		try {
			if (this.os==null) {
				this.openConnection();
			}
			this.os.write(code);
			this.os.flush();
		} catch (Throwable e) {
			this.cleanup();
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
			System.out.println(this.daguCarRemote.toString() +" currently not online: " + e.getMessage());
			return false;
		}
	}


}
