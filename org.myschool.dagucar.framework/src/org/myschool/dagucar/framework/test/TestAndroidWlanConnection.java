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

			remote.sendGoStraight(2);
			remote.releaseConnection();
		} catch (IOException e) {
			throw new AssertionError(e);
		}

	}

	@Test
	public void testAllCommands() throws InterruptedException {
		RemoteControl remote = RemoteControl.remoteViaTcp;
		try {
			DirectionCommand[] directions=DirectionCommand.values();
			SpeedCommand[] speeds=SpeedCommand.values();
			int milliseconds = 3000;
			for (DirectionCommand d:directions) {
				for (SpeedCommand s:speeds) {
					remote.sendSingleCommand(2, d, s, milliseconds);
				}
			}
			remote.releaseConnection();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
}
