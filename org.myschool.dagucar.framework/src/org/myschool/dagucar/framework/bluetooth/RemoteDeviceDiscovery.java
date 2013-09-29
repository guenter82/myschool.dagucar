package org.myschool.dagucar.framework.bluetooth;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.bluetooth.*;

import org.myschool.dagucar.framework.program.ServerContext;

/**
 * Minimal Device Discovery example.
 */
public class RemoteDeviceDiscovery implements Runnable {
	
	public final static RemoteDeviceDiscovery remoteDeviceDiscovery = new RemoteDeviceDiscovery();
	
    private final Object inquiryCompletedEvent = new Object();
    
    private RemoteDeviceDiscovery () {}

    private DiscoveryListener listener = new DiscoveryListener() {

        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        	String address = btDevice.getBluetoothAddress();
            System.out.println("Device " +address + " found");
            
            try {
            	String name = btDevice.getFriendlyName(true);
                System.out.println("     name " + name);
                //only add DaguCars
                if (ServerContext.dagucarDeviceName.equals(name)) {
                	String serviceUrl = "btspp://"+address+":1;authenticate=false;encrypt=false;master=false";
                	if (ServerContext.dagucarDevice1.equals(address)) {
                		ServerContext.dagucars.put(1, new DaguCar(1, btDevice, serviceUrl));
                	} else if (ServerContext.dagucarDevice2.equals(address)) {
                		ServerContext.dagucars.put(2, new DaguCar(2, btDevice, serviceUrl));
                	} else if (ServerContext.dagucarDevice3.equals(address)) {
                		ServerContext.dagucars.put(3, new DaguCar(3, btDevice, serviceUrl));
                	} else if (ServerContext.dagucarDevice4.equals(address)) {
                		ServerContext.dagucars.put(4, new DaguCar(4, btDevice, serviceUrl));
                	} else {
                		for (int i=5; i<100;i++) {
                			if (!ServerContext.dagucars.containsKey(i)) {
                				ServerContext.dagucars.put(i, new DaguCar(i, btDevice, serviceUrl));
                			}
                		}
                	}
                	
                }
            } catch (IOException cantGetDeviceName) {
            }
        }

        public void inquiryCompleted(int discType) {
            System.out.println("Device Inquiry completed!");
            synchronized (inquiryCompletedEvent){
                inquiryCompletedEvent.notifyAll();
            }
        }
        public void serviceSearchCompleted(int transID, int respCode) {
        }

        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        }
    };
    
    
    synchronized public void run() {
        try {
        	ServerContext.dagucars.clear();
			synchronized (inquiryCompletedEvent) {
				boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent()
						.startInquiry(DiscoveryAgent.GIAC, listener);
				if (started) {
					System.out.println("wait for device inquiry to complete...");
					inquiryCompletedEvent.wait();
					System.out.println(ServerContext.dagucars.size()	+ " device(s) found");
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}