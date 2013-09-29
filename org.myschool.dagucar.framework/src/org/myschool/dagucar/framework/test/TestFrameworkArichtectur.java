package org.myschool.dagucar.framework.test;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.myschool.dagucar.framework.command.DirectionCommand;
import org.myschool.dagucar.framework.command.SpeedCommand;
import org.myschool.dagucar.framework.program.ServerContext;
import org.myschool.dagucar.framework.program.StartDaguCarServer;
import org.myschool.dagucar.framework.remote.RemoteControl;

public class TestFrameworkArichtectur {

	RemoteControl remote = RemoteControl.remoteViaTcp;
	@BeforeClass
	public static void setUp() throws InterruptedException {
		StartDaguCarServer.main(new String[]{ServerContext.arg0});
		Thread.sleep(5000); //time for tcp server
	}
	

	@Test
	public void testLocalTcpConnection() throws InterruptedException {
		try {
			remote.activate("127.0.0.1");
			remote.sendGoStraight();
			Thread.sleep(500 * 1000);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	
	public void testAllCommands() throws InterruptedException {

		try {
			remote.activate("127.0.0.1");
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

	
	public void testToRemoteIP() {

		RemoteControl remote = RemoteControl.remoteViaTcp;
		try {
			remote.activate("10.63.208.218");
			remote.sendGoStraight();
			remote.shutDown();
		} catch (IOException e) {
			throw new AssertionError(e);
		}

	}

}
