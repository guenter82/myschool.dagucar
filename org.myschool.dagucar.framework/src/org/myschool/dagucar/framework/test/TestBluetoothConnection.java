package org.myschool.dagucar.framework.test;

import java.io.IOException;

import org.junit.Test;
import org.myschool.dagucar.framework.remote.RemoteControl;

public class TestBluetoothConnection {


	@Test
	public void connectionTest() {
		RemoteControl remote = RemoteControl.remoteViaBluetooth;
		try {
			remote.activate("");
			remote.sendGoStraight();
			remote.shutDown();

		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void reactivateTest() {
		RemoteControl remote = RemoteControl.remoteViaBluetooth;
		try {
			remote.activate("");
			remote.sendGoStraight();
			remote.shutDown();
			remote.activate("");
			remote.sendGoStraight();
			remote.shutDown();

		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	public void wrongReactivationTest() {
		RemoteControl remote = RemoteControl.remoteViaBluetooth;
		try {
			remote.activate("");
			remote.sendGoStraight();
			remote.activate("");
			remote.sendGoStraight();
			remote.shutDown();

		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

}
