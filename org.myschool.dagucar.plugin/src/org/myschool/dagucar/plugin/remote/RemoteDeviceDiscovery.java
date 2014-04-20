package org.myschool.dagucar.plugin.remote;

import java.io.IOException;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 * Minimal Device Discovery example.
 */
public class RemoteDeviceDiscovery implements Runnable {

	public static final String dagucarDeviceName = "DaguCar";
	public String serviceUrl="";

	public final static RemoteDeviceDiscovery remoteDeviceDiscovery = new RemoteDeviceDiscovery();

	private final Object inquiryCompletedEvent = new Object();

	private RemoteDeviceDiscovery () {}

	private DiscoveryListener listener = new DiscoveryListener() {

		@Override
		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			String address = btDevice.getBluetoothAddress();
			System.out.println("Device " +address + " found");

			try {
				String name = btDevice.getFriendlyName(true);
				System.out.println("     name " + name);
				//only add DaguCars
				if (dagucarDeviceName.equals(name)) {
					RemoteDeviceDiscovery.this.serviceUrl = "btspp://"+address+":1;authenticate=false;encrypt=false;master=false";
				}
			} catch (IOException cantGetDeviceName) {
			}
		}

		@Override
		public void inquiryCompleted(int discType) {
			System.out.println("Device Inquiry completed!");
			synchronized (RemoteDeviceDiscovery.this.inquiryCompletedEvent){
				RemoteDeviceDiscovery.this.inquiryCompletedEvent.notifyAll();
			}
		}
		@Override
		public void serviceSearchCompleted(int transID, int respCode) {
		}

		@Override
		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		}
	};


	@Override
	synchronized public void run() {
		try {
			synchronized (this.inquiryCompletedEvent) {
				boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent()
						.startInquiry(DiscoveryAgent.GIAC, this.listener);
				if (started) {
					System.out.println("wait for device inquiry to complete...");
					this.inquiryCompletedEvent.wait();
					System.out.println("DaguCar device found");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}