package dagucarmodule;

import java.io.IOException;

import org.myschool.dagucar.plugin.remote.DaguCarRemote;

public class DaguCarBluetooth {
	final DaguCarRemote remote;

	final Runnable finalzier=new Runnable() {

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.close();

		}

		public void close() {
			try {
				while (!DaguCarBluetooth.this.remote.isIdle()) {
					Thread.sleep(2000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			DaguCarBluetooth.this.remote.close();
		}
	};

	public DaguCarBluetooth(int number) {
		this.remote =  new DaguCarRemote(number);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//		Thread t = new Thread(this.finalzier);
		//		t.start();
	}

	public void send(byte code, int milliseconds) {
		try {
			this.remote.sendNextByte(code, milliseconds);
			Thread.sleep(milliseconds-10);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			while (!DaguCarBluetooth.this.remote.isIdle()) {
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DaguCarBluetooth.this.remote.close();
	}

}
