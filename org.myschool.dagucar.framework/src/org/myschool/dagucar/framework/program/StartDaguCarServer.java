package org.myschool.dagucar.framework.program;

import org.myschool.dagucar.framework.bluetooth.DaguCar;
import org.myschool.dagucar.framework.bluetooth.RemoteDeviceDiscovery;
import org.myschool.dagucar.framework.tcpserver.TcpServer;

public class StartDaguCarServer {

	public static void main(String[] args) {
		acquireDaguCars(args);
		// Start up TCP Server 
		TcpServer server = new TcpServer();
		Thread serverThread = new Thread(server, "DaguCar-Manager-Tcp-Thread");
		serverThread.setDaemon(false);
		serverThread.start();

	}

	private static void acquireDaguCars(String[] args) {
		if (args.length>0) { //ids provided
			if (ServerContext.arg0.equals( args[0] ) ) {
				ServerContext.dagucars.put(1, new DaguCar(1, null, "btspp://"+ServerContext.dagucarDevice1+":1;authenticate=false;encrypt=false;master=false"));
				ServerContext.dagucars.put(2, new DaguCar(2, null, "btspp://"+ServerContext.dagucarDevice2+":1;authenticate=false;encrypt=false;master=false"));
				ServerContext.dagucars.put(3, new DaguCar(3, null, "btspp://"+ServerContext.dagucarDevice3+":1;authenticate=false;encrypt=false;master=false"));
				ServerContext.dagucars.put(4, new DaguCar(4, null, "btspp://"+ServerContext.dagucarDevice4+":1;authenticate=false;encrypt=false;master=false"));
			}
		} else {
			//save all dagu cars
			RemoteDeviceDiscovery deviceDiscovery=RemoteDeviceDiscovery.remoteDeviceDiscovery;
			deviceDiscovery.run(); //give time to create connections
		}
	}

}
