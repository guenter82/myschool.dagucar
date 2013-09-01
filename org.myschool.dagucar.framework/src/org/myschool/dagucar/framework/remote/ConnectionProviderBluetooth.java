package org.myschool.dagucar.framework.remote;

import java.io.IOException;
import java.io.OutputStream;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class ConnectionProviderBluetooth implements ConnectionProvider {

	/**
     * Every physical DaguCar might have a different service URL please use the Bluetooth Command Line Tools for Windows
     * and there the command 'btdiscovery' (to find under: http://www.filecluster.com/System-Utilities/System-Maintenance/Download-Bluetooth-Command-Line-Tools.html)
     * or other means to discovery bluetooth devices and services - eg your bluetooth driver migth come with equivalent tools!
     */
    private String serviceURL="btspp://001210240245:1;authenticate=false;encrypt=false;master=false"; //DaguCar Service

	private StreamConnection con;

	@Override
	public OutputStream openConnection(Object connectionParameter) throws IOException {
		System.out.println("Used Bluetooth Stack: " + LocalDevice.getProperty("bluecove.stack"));
        if (this.serviceURL==null) {
            throw new IllegalStateException("Service URL must not be null when cmd is sent!");
        }
	    /*
        * Open the connection to the bluetooth server
        */
		this.con =(StreamConnection)Connector.open(serviceURL);
        /*
        * Sends data to remote device
        */
		OutputStream os = con.openOutputStream();
		return os;
	}

	@Override
	public void closeConnection(OutputStream os) throws IOException {
		if (os!=null) os.close();
		if (con!=null) con.close();
		con = null;
		System.out.println("Bluetooth connection to DaguCar was safely closed");
	}

}
