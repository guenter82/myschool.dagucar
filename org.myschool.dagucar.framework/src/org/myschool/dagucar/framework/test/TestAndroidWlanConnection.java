package org.myschool.dagucar.framework.test;

import java.io.IOException;

import org.junit.Test;
import org.myschool.dagucar.framework.command.DirectionCommand;
import org.myschool.dagucar.framework.command.SpeedCommand;
import org.myschool.dagucar.framework.remote.RemoteControl;

public class TestAndroidWlanConnection {

	public void testToRemoteIP() {

		RemoteControl remote = RemoteControl.remoteViaTcp;
		try {
			remote.activate("192.168.1.100");
			remote.sendGoStraight();
			remote.shutDown();
		} catch (IOException e) {
			throw new AssertionError(e);
		}

	}
	
	@Test
	public void testAllCommands() throws InterruptedException {
		RemoteControl remote = RemoteControl.remoteViaTcp;
		try {
			remote.activate("192.168.1.100");
			DirectionCommand[] directions=DirectionCommand.values();
			SpeedCommand[] speeds=SpeedCommand.values();
			int milliseconds = 3000;
			for (DirectionCommand d:directions) {
				for (SpeedCommand s:speeds) {
					remote.sendSingleCommand(d, s, milliseconds);
				}
			}
			remote.shutDown();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
}
