package org.myschool.dagucar.plugin.remote;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BluetoothCommunicator {

	private ConnectionWrapper con;
	private OutputStream os;
	private final DaguCarRemote daguCarRemote;
	private final String serviceURL;

	public BluetoothCommunicator(DaguCarRemote daguCar) {
		this.daguCarRemote = daguCar;
		this.serviceURL = daguCar.getServiceUrl();

	}

	private OutputStream openConnection() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		//System.out.println("Used Bluetooth Stack: " + LocalDevice.getProperty("bluecove.stack"));
		if (this.serviceURL==null) {
			throw new IllegalStateException("Service URL must not be null when cmd is sent!");
		}
		/*
		 * Open the connection to the bluetooth server
		 */
		Class<?> connectorClass= this.getClass().getClassLoader().loadClass("javax.microedition.io.Connector");
		this.getClass().getClassLoader().loadClass("com.intel.bluetooth.BluetoothRFCommClientConnection");
		Method openMethod=connectorClass.getDeclaredMethod("open", String.class);
		Object object = openMethod.invoke(null, this.serviceURL.toString());
		this.con = new ConnectionWrapper(object);

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
			try {
				this.openConnection();
				this.os.write(code);
				this.os.flush();
			} catch (Exception e2) {
				throw new IOException(e2);
			}
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
			System.out.println(this.daguCarRemote.toString() +" currently not online: " + e.getLocalizedMessage());
			return false;
		}
	}


}
